package gbsoft.com.dental_gb;

import java.util.ArrayList;

public class GoldDTO {
    private int id;
    private String clientName;
    private String goldName;
    private double stockGold;
    private ArrayList<GoldInOutputHistoryDTO> historyDTOS;

    public class GoldInOutputHistoryDTO{
        private int id;
        private String date;
        private String history;
        private double consumption;
        private double stockGold;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getHistory() {
            return history;
        }

        public void setHistory(String history) {
            this.history = history;
        }

        public double getConsumption() {
            return consumption;
        }

        public void setConsumption(double consumption) {
            this.consumption = consumption;
        }

        public double getStockGold() {
            return stockGold;
        }

        public void setStockGold(double stockGold) {
            this.stockGold = stockGold;
        }
    }

    public ArrayList<GoldInOutputHistoryDTO> getHistoryDTOS() {
        return historyDTOS;
    }

    public void setHistoryDTOS(ArrayList<GoldInOutputHistoryDTO> historyDTOS) {
        this.historyDTOS = historyDTOS;
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

    public String getGoldName() {
        return goldName;
    }

    public void setGoldName(String goldName) {
        this.goldName = goldName;
    }

    public double getStockGold() {
        return stockGold;
    }

    public void setStockGold(double stockGold) {
        this.stockGold = stockGold;
    }
}
