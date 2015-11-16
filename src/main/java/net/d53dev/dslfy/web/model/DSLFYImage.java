package net.d53dev.dslfy.web.model;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.modelmapper.internal.cglib.core.Local;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * Created by davidsere on 16/11/15.
 */
@Entity
public class DSLFYImage implements Comparable<DSLFYImage>{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String descriptor;

    private String properties;

    private LocalDateTime createDate;
    private LocalDateTime uploadDate;
    private LocalDateTime processingDate;

    @Transient
    private DSLFYImageData imageData;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public DSLFYImageData getImageData() {
        return imageData;
    }

    public void setImageData(DSLFYImageData imageData) {
        this.imageData = imageData;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public LocalDateTime getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(LocalDateTime processingDate) {
        this.processingDate = processingDate;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    @Override
    public int compareTo(DSLFYImage o) {
        return CompareToBuilder.reflectionCompare(this, o);
    }
}
