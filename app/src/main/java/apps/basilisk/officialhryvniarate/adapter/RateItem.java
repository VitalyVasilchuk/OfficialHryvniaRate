package apps.basilisk.officialhryvniarate.adapter;

public class RateItem{
    private String name;
    private int codeDigital;
    private String codeLiteral;
    private double value;
    private double difference;
    private String date;
    private boolean favorite;

    public RateItem(String name, int codeDigital, String codeLiteral, double value, double difference, String date, boolean favorite) {
        this.name = name;
        this.codeDigital = codeDigital;
        this.codeLiteral = codeLiteral;
        this.value = value;
        this.difference = difference;
        this.date = date;
        this.favorite = favorite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCodeDigital() {
        return codeDigital;
    }

    public void setCodeDigital(int codeDigital) {
        this.codeDigital = codeDigital;
    }

    public String getCodeLiteral() {
        return codeLiteral;
    }

    public void setCodeLiteral(String codeLiteral) {
        this.codeLiteral = codeLiteral;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getDifference() {
        return difference;
    }

    public void setDifference(double difference) {
        this.difference = difference;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "RateItem{" +
                "name='" + name + '\'' +
                ", codeDigital=" + codeDigital +
                ", codeLiteral='" + codeLiteral + '\'' +
                ", value=" + value +
                ", difference=" + difference +
                ", date='" + date + '\'' +
                ", favorite=" + favorite +
                '}';
    }
}
