package project5.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.time.LocalDate;

@XmlRootElement
public class Task {
    @XmlElement
    private String id;
    @XmlElement
    private String title;
    @XmlElement
    private String description;
    @XmlElement
    private int stateId;
    @XmlElement
    private int priority;
    @XmlElement
    private LocalDate startDate;
    @XmlElement
    private LocalDate limitDate;
    @XmlElement
    public static final int TODO = 100;
    @XmlElement
    public static final int DOING = 200;
    @XmlElement
    public static final int DONE = 300;
    @XmlElement
    public static final int LOWPRIORITY = 100;
    @XmlElement
    public static final int MEDIUMPRIORITY = 200;
    @XmlElement
    public static final int HIGHPRIORITY = 300;
    @XmlElement
    public Category category;
    @XmlElement
    public boolean erased;
    @XmlElement
    public User owner;
    @XmlElement
    public LocalDate doneDate;

    private static final long serialVersionUID = 1L;


    public Task() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void generateId() {
        this.id = String.valueOf(System.currentTimeMillis());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public void setInitialStateId() {
        this.stateId = TODO;
    }

    public void editStateId(int stateId) {
        if (stateId == TODO) {
            this.stateId = TODO;
        } else if (stateId == DOING) {
            this.stateId = DOING;
        } else {
            this.stateId = DONE;
        }

    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        if (priority == LOWPRIORITY) {
            this.priority = LOWPRIORITY;
        } else if (priority == MEDIUMPRIORITY) {
            this.priority = MEDIUMPRIORITY;
        } else if (priority == HIGHPRIORITY) {
            this.priority = HIGHPRIORITY;
        }
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(LocalDate limitDate) {
        this.limitDate = limitDate;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean getErased() {
        return erased;
    }

    public void setErased(boolean erased) {
        this.erased = erased;
    }

    public LocalDate getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(LocalDate doneDate) {
        this.doneDate = doneDate;
    }

    public int getDoneMonth() {
        if (doneDate != null) {
            return doneDate.getMonthValue();
        } else {
            return 0; // Ou outra indicação de que a data de conclusão não está definida
        }
    }

    public String getDoneMonthName() {
        if (doneDate != null) {
            int month = doneDate.getMonthValue();
            switch (month) {
                case 1:
                    return "Jan";
                case 2:
                    return "Feb";
                case 3:
                    return "Mar";
                case 4:
                    return "Apr";
                case 5:
                    return "May";
                case 6:
                    return "Jun";
                case 7:
                    return "Jul";
                case 8:
                    return "Aug";
                case 9:
                    return "Sep";
                case 10:
                    return "Oct";
                case 11:
                    return "Nov";
                case 12:
                    return "Dec";
            }
        }
        return "";
    }

    public int getDoneYear() {
        if (doneDate != null) {
            return doneDate.getYear();
        } else {
            return 0; // Ou outra indicação de que a data de conclusão não está definida
        }
    }

}