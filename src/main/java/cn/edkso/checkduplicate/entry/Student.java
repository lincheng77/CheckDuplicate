package cn.edkso.checkduplicate.entry;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String password;
    private String name;
    private Integer state;
    private Integer clazzId;
    private String clazzName;

    @CreatedDate
    private Timestamp createTime;

    @LastModifiedDate
    private Timestamp updateTime;

}
