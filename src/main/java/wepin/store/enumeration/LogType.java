package wepin.store.enumeration;

public enum LogType {
    ERROR("ERROR", "ERROR"),
    WARN("WARN", "WARN");

    String code;
    String name;

    LogType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

}
