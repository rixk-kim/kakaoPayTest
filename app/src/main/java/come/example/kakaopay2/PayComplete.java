package come.example.kakaopay2;

public class PayComplete {

    public String aid;
    public String tid;
    public String cid;
    public String sid;
    public String partner_order_id;
    public String partner_user_id;
    public String item_name;
    public String quantity;
    public Amount amount;
    public Card_info card_info;
    public String created_at;
    public String approved_at;

    private class Amount {
        public String total;
        public String tax_free;
        public String vat;
        public String point;
        public String discount;
        private Amount(String total, String tax_free, String vat, String point, String discount) {
            this.total = total;
            this.tax_free = tax_free;
            this.vat = vat;
            this.point = point;
            this.discount = discount;
        }
    }

    private  class Card_info {
        public String interest_free_install;
        public String bin;
        public String card_type;
        public String card_mid;
        public String approved_id;
        public String install_month;
        public String purchase_corp;
        public String purchase_corp_code;
        public String kakaopay_purchase_corp;
        public String kakaopay_purchase_corp_code;
        public String kakaopay_issuer_corp;
        public String kakaopay_issuer_corp_code;

        private Card_info(String interest_free_install, String bin, String card_type, String card_mid,
                          String approved_id, String install_month, String purchase_corp, String purchase_corp_code,
                          String kakaopay_purchase_corp, String kakaopay_purchase_corp_code, String kakaopay_issuer_corp,
                          String kakaopay_issuer_corp_code) {
            this.interest_free_install = interest_free_install;
            this.bin = bin;
            this.card_type = card_type;
            this.card_mid = card_mid;
            this.approved_id = approved_id;
            this.install_month = install_month;
            this.purchase_corp = purchase_corp;
            this.purchase_corp_code = purchase_corp_code;
            this.kakaopay_purchase_corp = kakaopay_purchase_corp;
            this.kakaopay_purchase_corp_code = kakaopay_purchase_corp_code;
            this.kakaopay_issuer_corp = kakaopay_issuer_corp;
            this.kakaopay_issuer_corp_code = kakaopay_issuer_corp_code;
        }
    }

    private PayComplete(String aid, String tid, String cid, String sid, String partner_order_id, String partner_user_id,
                        String item_name, String quantity, Amount amount, Card_info card_info,  String created_at, String approved_at) {
        this.aid = aid;
        this.tid = tid;
        this.cid = cid;
        this.sid = sid;
        this.partner_order_id = partner_order_id;
        this.partner_user_id = partner_user_id;
        this.item_name = item_name;
        this.quantity = quantity;
        this.amount = amount;
        this.card_info = card_info;
        this.created_at = created_at;
        this.approved_at = approved_at;
    }

    class PayLongCancel {
        String cid;
        String sid;
        String status;
        String created_at;
        String last_approved_at;
        String inactivated_at;
    }

    class PayLongSearch {
        String available;
        String cid;
        String status;
        String payment_method_type;
        String item_name;
        String created_at;
        String last_approved_at;
        String inactivated_at;
    }
}
