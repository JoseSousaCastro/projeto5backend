package project5.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement
public class RegistInfoUser {
    @XmlElement
    private int month;
    @XmlElement
    private int year;
    @XmlElement
    private int count;

    public RegistInfoUser() {
        // Required by JAXB
    }

    public RegistInfoUser(int month, int year, int count) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistInfoUser that = (RegistInfoUser) o;
        return month == that.month &&
                year == that.year;
    }

    @Override
    public int hashCode() {
        return Objects.hash(month, year);
    }
}
