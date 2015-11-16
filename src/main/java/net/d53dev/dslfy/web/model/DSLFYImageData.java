package net.d53dev.dslfy.web.model;

/**
 * Created by davidsere on 16/11/15.
 */
public class DSLFYImageData {

    private Long dslfyImageId;

    private byte[] imageData;

    public Long getDslfyImageId() {
        return dslfyImageId;
    }

    public void setDslfyImageId(Long dslfyImageId) {
        this.dslfyImageId = dslfyImageId;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
}
