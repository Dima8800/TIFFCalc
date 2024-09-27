package calc.square.imagecalc.models;

import java.time.LocalDateTime;
import java.util.List;

public class Result {

    private Long id;

    private String number;
    private Double totalPerimetr = 0D;
    private Double totalArea = 0D;
    private Integer totalFiles;

    private List<Photo> filles;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Double getTotalPerimetr() {
        return totalPerimetr;
    }

    public void setTotalPerimetr(Double totalPerimetr) {
        this.totalPerimetr = totalPerimetr;
    }

    public Double getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(Double totalArea) {
        this.totalArea = totalArea;
    }

    public Integer getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(Integer totalFiles) {
        this.totalFiles = totalFiles;
    }

    public List<Photo> getFilles() {
        return filles;
    }

    public void setFilles(List<Photo> filles) {
        this.filles = filles;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Result(Long id, String number, Double totalPerimetr, LocalDateTime createdAt, Integer totalFiles, Double totalArea, LocalDateTime updatedAt) {
        this.id = id;
        this.number = number;
        this.totalPerimetr = totalPerimetr;
        this.createdAt = createdAt;
        this.totalFiles = totalFiles;
        this.totalArea = totalArea;
        this.updatedAt = updatedAt;
    }

    public Result(){}
}
