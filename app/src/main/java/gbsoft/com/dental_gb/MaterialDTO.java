package gbsoft.com.dental_gb;

public class MaterialDTO {
    private int id; // DB: materialsCode(자재코드)
    private String materialName;
    private int inStock;
    private int outStock;
    private int stock;
    private String clientName; // 현재 안씀
    private String warehousingDate; // 현재 안씀
    private int warehousingVolume; // 현재 안씀

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public int getInStock() {
        return inStock;
    }

    public void setInStock(int inStock) {
        this.inStock = inStock;
    }

    public int getOutStock() {
        return outStock;
    }

    public void setOutStock(int outStock) {
        this.outStock = outStock;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getWarehousingDate() {
        return warehousingDate;
    }

    public void setWarehousingDate(String warehousingDate) {
        this.warehousingDate = warehousingDate;
    }

    public int getWarehousingVolume() {
        return warehousingVolume;
    }

    public void setWarehousingVolume(int warehousingVolume) {
        this.warehousingVolume = warehousingVolume;
    }
}
