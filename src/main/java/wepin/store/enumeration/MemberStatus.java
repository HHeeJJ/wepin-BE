package wepin.store.enumeration;

public enum MemberStatus {
    REQUEST("REQUEST", "REQUEST"),
    BLACK("BLACK", "BLACK"),
    CANCEL("CANCEL", "CANCEL");

    String code;
    String name;

    MemberStatus(String code, String name) {
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
