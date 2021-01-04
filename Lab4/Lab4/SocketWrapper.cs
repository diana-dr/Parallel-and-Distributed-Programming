using System;
using System.Diagnostics;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace Lab4
{
    public class SocketWrapper
    {
        // Client socket.
        public Socket socket = null;

        // ManualResetEvent instances signal completion.
        public readonly ManualResetEvent sendDone = new ManualResetEvent(false);
        public readonly ManualResetEvent connectDone = new ManualResetEvent(false);
        public readonly ManualResetEvent receiveDone = new ManualResetEvent(false);

        public string endpoint;
        public string hostname;
        public int id;
        public IPEndPoint remoteEndPoint;

        // The response from the remote device.
        public StringBuilder responseContent = new StringBuilder();

        // Receive buffer.
        public byte[] buffer = new byte[512];
    }
}
