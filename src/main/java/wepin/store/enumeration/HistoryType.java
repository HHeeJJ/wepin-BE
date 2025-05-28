package wepin.store.enumeration;

public enum HistoryType {
    FEED_CREATE("FEED_CREATE", "FEED_CREATE"),
    REPLY("REPLY", "REPLY"),
    FOLLOW("FOLLOW", "FOLLOW"),
    LIKE("LIKE", "LIKE"),
    DISLIKE("DISLIKE", "DISLIKE"),
    UNFOLLOW("UNFOLLOW", "UNFOLLOW");

    final String code;
    final String name;

    HistoryType(String code, String name) {
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
