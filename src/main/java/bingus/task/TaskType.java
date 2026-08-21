package bingus.task;

/**
 * Identifies the supported task categories and their display symbols.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    /**
     * Creates a task type with its display symbol.
     *
     * @param symbol symbol displayed for this type
     */
    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the symbol used when displaying this task type.
     *
     * @return task-type display symbol
     */
    public String getSymbol() {
        return symbol;
    }
}
