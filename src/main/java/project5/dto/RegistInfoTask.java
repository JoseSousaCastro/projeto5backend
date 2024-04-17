package project5.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegistInfoTask {
    @XmlElement
    private int month;
    @XmlElement
    private int year;
    @XmlElement
    private int count;

    public RegistInfoTask() {
        // Construtor vazio necessário para JAXB
    }

    public RegistInfoTask(int month, int year, int count) {
        this.month = month;
        this.year = year;
        this.count = count;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
