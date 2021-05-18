package cn.edkso.checkduplicate.entry;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.models.auth.In;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Clazz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private Integer grade;
    private String college;
    private String counselor;
    private Integer state;
    private Integer numbers;

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  //从前端接收时数据格式
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updateTime;
}
