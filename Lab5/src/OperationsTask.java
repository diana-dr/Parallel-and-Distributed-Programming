public class OperationsTask implements Runnable {

    private int start;
    private int end;
    private Polynomial polynomial1, polynomial2, resultPolynomial;

    public OperationsTask(int start, int end, Polynomial polynomial1, Polynomial polynomial2, Polynomial resultPolynomial) {
        this.start = start;
        this.end = end;
        this.polynomial1 = polynomial1;
        this.polynomial2 = polynomial2;
        this.resultPolynomial = resultPolynomial;
    }

    @Override
    public void run() {
        for (int index = start; index < end; index++) {

            //if the index is out of range
            if (index > resultPolynomial.getLength()) {
                return;
            }

            //find all the pairs that we add to obtain the value of a result coefficient
            for (int j = 0; j <= index; j++) {
                int i = index - j;
                if (j < polynomial1.getLength() && i < polynomial2.getLength()) {
                    int value = polynomial1.getCoefficients().get(j) * polynomial2.getCoefficients().get(i);
                    resultPolynomial.getCoefficients().set(index, resultPolynomial.getCoefficients().get(index) + value);
                }
            }
        }
    }
}
