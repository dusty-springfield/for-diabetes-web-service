package webservice.models.record;

import org.hibernate.annotations.GenericGenerator;
import webservice.models.user.User;

import javax.persistence.*;

@Entity
public class Record {
    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO,
            generator = "native"
    )
    @GenericGenerator(
            name = "native",
            strategy = "native"
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    private RecordType recordType;

    @Column(nullable = false)
    private double value;

    @Column(nullable = false)
    private long date;

    @Column
    private String description;

    public Record() {
    }

    public Record(User u, RecordType type, double value, long date, String description) {
        setUser(u);
        setRecordType(type);
        setValue(value);
        setDate(date);
        setDescription(description);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        user.addRecord(this);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
        recordType.addRecord(this);
    }
}
