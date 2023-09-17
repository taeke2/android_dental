package gbsoft.com.dental_gb;

import java.util.ArrayList;

public class RequestDTO {
    private int id; // DB: groupCode(그룹코드)
    private String boxNo = "";
    private String boxName = "";
    private String clientName;
    private String patientName = "";
    private String productName;
    private String orderDate;
    private String deadlineDate;
    private String deadlineTime;
    private String dentalFormula10;
    private String dentalFormula20;
    private String dentalFormula30;
    private String dentalFormula40;
    private String telNum;

    private ArrayList<RequestDetailDTO> requestDetailDTOS;

    public class RequestDetailDTO {
        private String productName;
        private String productPart;
        private int orderCk;
        private int processCk;
        private int outmntCk;
        private String workOrderDate;
        private String processProgressDate;
        private String progressCompleteDate;
        private String productOutDate;
        private String dent1;
        private String dent2;
        private String dent3;
        private String dent4;
        /**     1   1   1
         *      1   1   1
         *      1   1   1
         */
        private String shade1;
        private String shade2;
        private String shade3;
        private String shade4;
        private String shade5;
        private String shade6;
        private String shade7;
        private String shade8;
        private String shade9;


        private String implantCrown;
        private String spaceLack;
        private String ponticDesign;
        private String metalDesign;
        private String fullDesign;
        private String flexibleDenture;
        private String jaw;
        private String partialDenture;
        private String dentureExtra;
        private String memo;
        private String type;

        public int getOrderCk() {
            return orderCk;
        }

        public void setOrderCk(int orderCk) {
            this.orderCk = orderCk;
        }

        public int getProcessCk() {
            return processCk;
        }

        public void setProcessCk(int processCk) {
            this.processCk = processCk;
        }

        public int getOutmntCk() {
            return outmntCk;
        }

        public void setOutmntCk(int outmntCk) {
            this.outmntCk = outmntCk;
        }

        public String getShade1() {
            return shade1;
        }

        public void setShade1(String shade1) {
            this.shade1 = shade1;
        }

        public String getShade2() {
            return shade2;
        }

        public void setShade2(String shade2) {
            this.shade2 = shade2;
        }

        public String getShade3() {
            return shade3;
        }

        public void setShade3(String shade3) {
            this.shade3 = shade3;
        }

        public String getShade4() {
            return shade4;
        }

        public void setShade4(String shade4) {
            this.shade4 = shade4;
        }

        public String getShade5() {
            return shade5;
        }

        public void setShade5(String shade5) {
            this.shade5 = shade5;
        }

        public String getShade6() {
            return shade6;
        }

        public void setShade6(String shade6) {
            this.shade6 = shade6;
        }

        public String getShade7() {
            return shade7;
        }

        public void setShade7(String shade7) {
            this.shade7 = shade7;
        }

        public String getShade8() {
            return shade8;
        }

        public void setShade8(String shade8) {
            this.shade8 = shade8;
        }

        public String getShade9() {
            return shade9;
        }

        public void setShade9(String shade9) {
            this.shade9 = shade9;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductPart() {
            return productPart;
        }

        public void setProductPart(String productPart) {
            this.productPart = productPart;
        }

        public String getWorkOrderDate() {
            return workOrderDate;
        }

        public void setWorkOrderDate(String workOrderDate) {
            this.workOrderDate = workOrderDate;
        }

        public String getProcessProgressDate() {
            return processProgressDate;
        }

        public void setProcessProgressDate(String processProgressDate) {
            this.processProgressDate = processProgressDate;
        }

        public String getProgressCompleteDate() {
            return progressCompleteDate;
        }

        public void setProgressCompleteDate(String progressCompleteDate) {
            this.progressCompleteDate = progressCompleteDate;
        }

        public String getProductOutDate() {
            return productOutDate;
        }

        public void setProductOutDate(String productOutDate) {
            this.productOutDate = productOutDate;
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

        public String getImplantCrown() {
            return implantCrown;
        }

        public void setImplantCrown(String implantCrown) {
            this.implantCrown = implantCrown;
        }

        public String getSpaceLack() {
            return spaceLack;
        }

        public void setSpaceLack(String spaceLack) {
            this.spaceLack = spaceLack;
        }

        public String getPonticDesign() {
            return ponticDesign;
        }

        public void setPonticDesign(String ponticDesign) {
            this.ponticDesign = ponticDesign;
        }

        public String getMetalDesign() {
            return metalDesign;
        }

        public void setMetalDesign(String metalDesign) {
            this.metalDesign = metalDesign;
        }

        public String getFullDesign() {
            return fullDesign;
        }

        public void setFullDesign(String fullDesign) {
            this.fullDesign = fullDesign;
        }

        public String getFlexibleDenture() {
            return flexibleDenture;
        }

        public void setFlexibleDenture(String flexibleDenture) {
            this.flexibleDenture = flexibleDenture;
        }

        public String getJaw() {
            return jaw;
        }

        public void setJaw(String jaw) {
            this.jaw = jaw;
        }

        public String getPartialDenture() {
            return partialDenture;
        }

        public void setPartialDenture(String partialDenture) {
            this.partialDenture = partialDenture;
        }

        public String getDentureExtra() {
            return dentureExtra;
        }

        public void setDentureExtra(String dentureExtra) {
            this.dentureExtra = dentureExtra;
        }

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public ArrayList<RequestDetailDTO> getRequestDetailDTOS() {
        return requestDetailDTOS;
    }

    public void setRequestDetailDTOS(ArrayList<RequestDetailDTO> requestDetailDTOS) {
        this.requestDetailDTOS = requestDetailDTOS;
    }

    public String getTelNum() {
        return telNum;
    }

    public void setTelNum(String telNum) {
        this.telNum = telNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBoxNo() { return boxNo; }

    public void setBoxNo(String boxNo) { this.boxNo = boxNo; }

    public String getBoxName() { return boxName; }

    public void setBoxName(String boxName) { this.boxName = boxName; }

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

    public String getDeadlineTime() {
        return deadlineTime;
    }

    public void setDeadlineTime(String deadlineTime) {
        this.deadlineTime = deadlineTime;
    }

    public String getDentalFormula10() {
        return dentalFormula10;
    }

    public void setDentalFormula10(String dentalFormula10) {
        this.dentalFormula10 = dentalFormula10;
    }

    public String getDentalFormula20() {
        return dentalFormula20;
    }

    public void setDentalFormula20(String dentalFormula20) {
        this.dentalFormula20 = dentalFormula20;
    }

    public String getDentalFormula30() {
        return dentalFormula30;
    }

    public void setDentalFormula30(String dentalFormula30) {
        this.dentalFormula30 = dentalFormula30;
    }

    public String getDentalFormula40() {
        return dentalFormula40;
    }

    public void setDentalFormula40(String dentalFormula40) {
        this.dentalFormula40 = dentalFormula40;
    }
}
