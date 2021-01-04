using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace Lab4
{
    public class TaskAsync
    {
        private static List<string> _hostNames;

        public static void Run(List<string> hostnames)
        {
            _hostNames = hostnames;
            var tasks = new List<Task>();

            for (var i = 0; i < hostnames.Count; i++)
            {
                tasks.Add(Task.Factory.StartNew(DoStart, i));
            }

            Task.WaitAll(tasks.ToArray());
        }

        private static void DoStart(object idObject)
        {
            var id = (int)idObject;
            StartClient(_hostNames[id], id);
        }

        private static async void StartClient(string host, int id)
        {
            // Establish the remote endpoint for the socket.
            var ipHostInfo = Dns.GetHostEntry(host.Split('/')[0]);
            var ipAddress = ipHostInfo.AddressList[0];
            var remoteEndpoint = new IPEndPoint(ipAddress, Utils.PORT);

            // Create a TCP/IP socket.
            var client = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

            var state = new SocketWrapper
            {
                socket = client,
                hostname = host.Split('/')[0],
                endpoint = host.Contains("/") ? host.Substring(host.IndexOf("/", StringComparison.Ordinal)) : "/",
                remoteEndPoint = remoteEndpoint,
                id = id
            };

            await ConnectWrapper(state);

            string requestString = Utils.GetRequestString(state.hostname, state.endpoint);

            await SendWrapper(state, requestString);
            await ReceiveWrapper(state);

            Console.WriteLine("#{0}: Content length is: {1}", id, Utils.GetContentLength(state.responseContent.ToString()));

            // Release the socket. 
            client.Shutdown(SocketShutdown.Both);
            client.Close();
        }

        private static async Task ConnectWrapper(SocketWrapper state)
        {
            // Connect to the remote endpoint.
            state.socket.BeginConnect(state.remoteEndPoint, ConnectCallback, state);

            await Task.FromResult<object>(state.connectDone.WaitOne());
        }

        private static void ConnectCallback(IAsyncResult asyncResult)
        {
            // Retrieve the socket from the state object.
            var state = (SocketWrapper)asyncResult.AsyncState;
            var clientSocket = state.socket;
            var clientId = state.id;

            // Complete the connection. 
            clientSocket.EndConnect(asyncResult);

            Console.WriteLine("#{0}: Socket connected to {1}", clientId, clientSocket.RemoteEndPoint);

            // Signal that the connection has been made.  
            state.connectDone.Set();
        }

        private static async Task SendWrapper(SocketWrapper state, string data)
        {
            // Convert the string data to byte data using ASCII encoding.  
            var requestBytes = Encoding.ASCII.GetBytes(data);

            // Begin sending the data to the remote device.  
            state.socket.BeginSend(requestBytes, 0, requestBytes.Length, 0, WhenSent, state);

            await Task.FromResult<object>(state.sendDone.WaitOne());
        }

        private static void WhenSent(IAsyncResult asyncResult)
        {
            var state = (SocketWrapper)asyncResult.AsyncState;
            var clientSocket = state.socket;
            var clientId = state.id;
            var bytesSent = clientSocket.EndSend(asyncResult);

            Console.WriteLine("#{0}: Sent {1} bytes to server.", clientId, bytesSent);

            state.sendDone.Set();
        }

        private static async Task ReceiveWrapper(SocketWrapper state)
        {
            // Begin receiving the data from the remote device. 
            state.socket.BeginReceive(state.buffer, 0, 512, 0, ReceiveCallback, state);

            await Task.FromResult<object>(state.receiveDone.WaitOne());
        }

        private static void ReceiveCallback(IAsyncResult asyncResult)
        {
            // Retrieve the state object and the client socket
            // from the asynchronous state object.
            var state = (SocketWrapper)asyncResult.AsyncState;
            var clientSocket = state.socket;

            try
            {
                // Read data from the remote device.
                var bytesRead = clientSocket.EndReceive(asyncResult);

                // There might be more data, so store the data received so far. 
                state.responseContent.Append(Encoding.ASCII.GetString(state.buffer, 0, bytesRead));

                if (!Utils.ResponseHeaderObtained(state.responseContent.ToString()))
                {
                    // Get the rest of the data. 
                    clientSocket.BeginReceive(state.buffer, 0, 512, 0, ReceiveCallback, state);
                }
                else
                {
                    // Write the response to the console. 
                    //Utils.PrintResponse(state);

                    // Signal that all bytes have been received.
                    state.receiveDone.Set();
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }
    }
}
