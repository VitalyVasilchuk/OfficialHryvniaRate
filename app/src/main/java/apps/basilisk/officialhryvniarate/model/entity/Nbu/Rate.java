package apps.basilisk.officialhryvniarate.model.entity.Nbu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rate {

    @SerializedName("r030")
    @Expose
    private Integer codeDigital;

    @SerializedName("txt")
    @Expose
    private String name;

    @SerializedName("rate")
    @Expose
    private Double value;

    @SerializedName("cc")
    @Expose
    private String codeLiteral;

    @SerializedName("exchangedate")
    @Expose
    private String date;

    public Integer getCodeDigital() {
        return codeDigital;
    }

    public void setCodeDigital(Integer codeDigital) {
        this.codeDigital = codeDigital;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getCodeLiteral() {
        return codeLiteral;
    }

    public void setCodeLiteral(String codeLiteral) {
        this.codeLiteral = codeLiteral;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Rate{" +
                "codeDigital=" + codeDigital +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", codeLiteral='" + codeLiteral + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
