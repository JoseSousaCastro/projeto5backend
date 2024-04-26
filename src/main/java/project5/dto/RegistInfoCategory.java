package project5.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegistInfoCategory {
    @XmlElement
    private String category;
    @XmlElement
    private int quantity;

    private static final long serialVersionUID = 1L;


    public RegistInfoCategory() {
        // Construtor vazio necessário para JAXB
    }

    public RegistInfoCategory(String category) {
        this.category = category;
        this.quantity = 1;
    }

    public RegistInfoCategory(String categoryName, int taskCount) {
        this.category = categoryName;
        this.quantity = taskCount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void increment() {
        this.quantity++;
    }
}

