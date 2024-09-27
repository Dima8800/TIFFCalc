package calc.square.imagecalc.models;

public class Photo {
    private Long id;

    private String nameFile;

    private Double Perimetr;
    private Double Area;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public Double getPerimetr() {
        return Perimetr;
    }

    public void setPerimetr(Double perimetr) {
        Perimetr = perimetr;
    }

    public Double getArea() {
        return Area;
    }

    public void setArea(Double area) {
        Area = area;
    }
}
