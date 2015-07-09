
package so.contacts.hub.ui.web.kuaidi.bean;

public class InitDataBean {
    private String source;
    private String orderFrom;
    private String orderTo;
    private String mobile;
    private Module module;

    public InitDataBean(String source, String mobile, String orderFrom, String orderTo,
            Module module) {
        this.source = source;
        this.mobile = mobile;
        this.orderFrom = orderFrom;
        this.orderTo = orderTo;
        this.module = module;
    }

    public InitDataBean(String source, String mobile,
            Module module) {
        this.source = source;
        this.mobile = mobile;
        this.module = module;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getOrderFrom() {
        return orderFrom;
    }

    public void setOrderFrom(String orderFrom) {
        this.orderFrom = orderFrom;
    }

    public String getOrderTo() {
        return orderTo;
    }

    public void setOrderTo(String orderTo) {
        this.orderTo = orderTo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public static class Module {
        private boolean orderHis;
        private boolean order2Pay;
        private boolean home400;

        public Module(boolean orderHis, boolean order2Pay, boolean home400) {
            this.orderHis = orderHis;
            this.order2Pay = order2Pay;
            this.home400 = home400;
        }

        public boolean isOrderHis() {
            return orderHis;
        }

        public void setOrderHis(boolean orderHis) {
            this.orderHis = orderHis;
        }

        public boolean isOrder2Pay() {
            return order2Pay;
        }

        public void setOrder2Pay(boolean order2Pay) {
            this.order2Pay = order2Pay;
        }

        public boolean isHome400() {
            return home400;
        }

        public void setHome400(boolean home400) {
            this.home400 = home400;
        }

    }

}
