package gbsoft.com.dental_gb;

import java.util.ArrayList;

public class ReleaseDTO {
    private int id; // DB: groupCode(의뢰서 그룹코드) -> requestCode로 현재 변경
    private String clientName;
    private String patientName;
    private String productName;
    private String orderDate; // DB: requestDate
    private String deadlineDate; // DB: dueDate
    private String dueTime;
    private String outDate;
    private int releaseYn; // DB: 출고 여부 확인
    private ArrayList<ReleaseProductDTO> releaseProductDTOS;

    public class ReleaseProductDTO{
        private String productName;
        private String managerName;
        private String orderDateTime;
        private String outDateTime;
        private String dent1;
        private String dent2;
        private String dent3;
        private String dent4;

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getManagerName() {
            return managerName;
        }

        public void setManagerName(String managerName) {
            this.managerName = managerName;
        }

        public String getOrderDateTime() {
            return orderDateTime;
        }

        public void setOrderDateTime(String orderDateTime) {
            this.orderDateTime = orderDateTime;
        }

        public String getOutDateTime() {
            return outDateTime;
        }

        public void setOutDateTime(String outDateTime) {
            this.outDateTime = outDateTime;
        }

        public String getDent1() {
            return dent1;
        }

        public void setDent1(String dent1) {
            this.dent1 = dent1;
        }

        public String getDent2() {
            return dent2;
        }

        public void setDent2(String dent2) {
            this.dent2 = dent2;
        }

        public String getDent3() {
            return dent3;
        }

        public void setDent3(String dent3) {
            this.dent3 = dent3;
        }

        public String getDent4() {
            return dent4;
        }

        public void setDent4(String dent4) {
            this.dent4 = dent4;
        }
    }

    public ArrayList<ReleaseProductDTO> getReleaseProductDTOS() {
        return releaseProductDTOS;
    }

    public void setReleaseProductDTOS(ArrayList<ReleaseProductDTO> releaseProductDTOS) {
        this.releaseProductDTOS = releaseProductDTOS;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(String deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public String getOutDate() {
        return outDate;
    }

    public void setOutDate(String outDate) {
        this.outDate = outDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public int getReleaseYn() {
        return releaseYn;
    }

    public void setReleaseYn(int releaseYn) {
        this.releaseYn = releaseYn;
    }
}
