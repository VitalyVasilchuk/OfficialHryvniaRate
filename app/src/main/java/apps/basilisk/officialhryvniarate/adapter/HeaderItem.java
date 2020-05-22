package apps.basilisk.officialhryvniarate.adapter;

public class HeaderItem {
    private String title;

    public HeaderItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "HeaderItem{" +
                "title='" + title + '\'' +
                '}';
    }
}
