package co.lujun.ghouse.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by lujun on 2015/8/3.
 */
public class Bill extends DataSupport {

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public long getLatesttime() {
        return latesttime;
    }

    public void setLatesttime(long latesttime) {
        this.latesttime = latesttime;
    }

    public boolean isconfirm() {
        return isconfirm;
    }

    public void setIsconfirm(boolean isconfirm) {
        this.isconfirm = isconfirm;
    }

    public long getAddperson() {
        return addperson;
    }

    public void setAddperson(long addperson) {
        this.addperson = addperson;
    }

    public long getConfirmperson() {
        return confirmperson;
    }

    public void setConfirmperson(long confirmperson) {
        this.confirmperson = confirmperson;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    private long id;

    private String content;

    private String summary;

    private long time;

    private String code;

    private float total;

    private String extra;

    private long latesttime;

    private boolean isconfirm;

    private long addperson;

    private long confirmperson;

    private String invoice;
}
