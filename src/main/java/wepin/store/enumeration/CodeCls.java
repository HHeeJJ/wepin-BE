package wepin.store.enumeration;

public enum CodeCls {
    BLOCK("BLOCK", "BLOCK");

    String code;
    String name;

    CodeCls(String code, String name) {
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
