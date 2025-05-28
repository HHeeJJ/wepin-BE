package wepin.store.enumeration;

public enum MemberChannel {
    EMAIL("EMAIL", "이메일"),
    KAKAO("KAKAO", "카카오"),
    NAVER("NAVER", "네이버"),
    APPLE("APPLE", "애플"),
    GOOGLE("GOOGLE", "구글");

    String code;
    String name;

    MemberChannel(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getCode() {
        return this.code;
    }

    public String toString() {
        return super.toString();
    }
}
