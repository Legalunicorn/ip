public enum TaskType {
    // types with their constructors
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    TaskType(String symobol) {
        this.symbol = symobol;
    }

    public String getSymbol() {
        return symbol;
    }
}
