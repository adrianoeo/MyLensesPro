package com.aeo.mylensespro.vo;

import java.util.Date;

public class TimeLensesVO {

    private String id;
    private String objectId;
    private String dateLeft;
    private String dateRight;
    private int expirationLeft;
    private int expirationRight;
    private int typeLeft;
    private int typeRight;
    private int inUseLeft;
    private int inUseRight;
    private int numDaysNotUsedLeft;
    private int numDaysNotUsedRight;
    private int qtdLeft;
    private int qtdRight;
    private Date dateCreate;

    public TimeLensesVO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    public String getDateLeft() {
        return dateLeft;
    }

    public void setDateLeft(String dateLeft) {
        this.dateLeft = dateLeft;
    }

    public String getDateRight() {
        return dateRight;
    }

    public void setDateRight(String dateRight) {
        this.dateRight = dateRight;
    }

    public int getExpirationLeft() {
        return expirationLeft;
    }

    public void setExpirationLeft(int expirationLeft) {
        this.expirationLeft = expirationLeft;
    }

    public int getExpirationRight() {
        return expirationRight;
    }

    public void setExpirationRight(int expirationRight) {
        this.expirationRight = expirationRight;
    }

    public int getTypeLeft() {
        return typeLeft;
    }

    public void setTypeLeft(int typeLeft) {
        this.typeLeft = typeLeft;
    }

    public int getTypeRight() {
        return typeRight;
    }

    public void setTypeRight(int typeRight) {
        this.typeRight = typeRight;
    }

    public int getInUseLeft() {
        return inUseLeft;
    }

    public void setInUseLeft(int inUseLeft) {
        this.inUseLeft = inUseLeft;
    }

    public int getInUseRight() {
        return inUseRight;
    }

    public void setInUseRight(int inUseRight) {
        this.inUseRight = inUseRight;
    }

    public int getNumDaysNotUsedLeft() {
        return numDaysNotUsedLeft;
    }

    public void setNumDaysNotUsedLeft(int numDaysNotUsedLeft) {
        this.numDaysNotUsedLeft = numDaysNotUsedLeft;
    }

    public int getNumDaysNotUsedRight() {
        return numDaysNotUsedRight;
    }

    public void setNumDaysNotUsedRight(int numDaysNotUsedRight) {
        this.numDaysNotUsedRight = numDaysNotUsedRight;
    }

    public int getQtdLeft() {
        return qtdLeft;
    }

    public void setQtdLeft(int qtdLeft) {
        this.qtdLeft = qtdLeft;
    }

    public int getQtdRight() {
        return qtdRight;
    }

    public void setQtdRight(int qtdRight) {
        this.qtdRight = qtdRight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeLensesVO that = (TimeLensesVO) o;

        if (expirationLeft != that.expirationLeft) return false;
        if (expirationRight != that.expirationRight) return false;
        if (typeLeft != that.typeLeft) return false;
        if (typeRight != that.typeRight) return false;
        if (inUseLeft != that.inUseLeft) return false;
        if (inUseRight != that.inUseRight) return false;
        if (numDaysNotUsedLeft != that.numDaysNotUsedLeft) return false;
        if (numDaysNotUsedRight != that.numDaysNotUsedRight) return false;
        if (qtdLeft != that.qtdLeft) return false;
        if (qtdRight != that.qtdRight) return false;
        if (!id.equals(that.id)) return false;
        if (!dateLeft.equals(that.dateLeft)) return false;
        if (!dateRight.equals(that.dateRight)) return false;
        return dateCreate.equals(that.dateCreate);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + dateLeft.hashCode();
        result = 31 * result + dateRight.hashCode();
        result = 31 * result + expirationLeft;
        result = 31 * result + expirationRight;
        result = 31 * result + typeLeft;
        result = 31 * result + typeRight;
        result = 31 * result + inUseLeft;
        result = 31 * result + inUseRight;
        result = 31 * result + numDaysNotUsedLeft;
        result = 31 * result + numDaysNotUsedRight;
        result = 31 * result + qtdLeft;
        result = 31 * result + qtdRight;
        result = 31 * result + dateCreate.hashCode();
        return result;
    }
}
