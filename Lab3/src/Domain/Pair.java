package Domain;

public class Pair<Key, Value>{

    private final Key firstValue;
    private final Value secondValue;

    public Pair(Key firstValue, Value secondValue) {
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }

    public Key getFirstValue() {
        return this.firstValue;
    }

    public Value getSecondValue() {
        return this.secondValue;
    }
}
