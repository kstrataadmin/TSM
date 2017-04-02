package com.kstrata.apps.tsm.business.dao.entity;


/**
 * TimesheetId entity. @author MyEclipse Persistence Tools
 */
public class TimesheetId implements
		java.io.Serializable {

	// Constructors

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer empProjectId;
	private Integer periodId;

	/** default constructor */
	public TimesheetId() {
	}

	/** full constructor */
	public TimesheetId(Integer empProjectId, Integer periodId) {
		this.empProjectId = empProjectId;
		this.periodId = periodId;
	}
	
	public Integer getEmpProjectId() {
		return this.empProjectId;
	}

	public void setEmpProjectId(Integer empProjectId) {
		this.empProjectId = empProjectId;
	}

	public Integer getPeriodId() {
		return this.periodId;
	}

	public void setPeriodId(Integer periodId) {
		this.periodId = periodId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TimesheetId))
			return false;
		TimesheetId castOther = (TimesheetId) other;

		return ((this.getEmpProjectId() == castOther.getEmpProjectId()) || (this
				.getEmpProjectId() != null
				&& castOther.getEmpProjectId() != null && this
				.getEmpProjectId().equals(castOther.getEmpProjectId())))
				&& ((this.getPeriodId() == castOther.getPeriodId()) || (this
						.getPeriodId() != null
						&& castOther.getPeriodId() != null && this
						.getPeriodId().equals(castOther.getPeriodId())));
	}

	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getEmpProjectId() == null ? 0 : this.getEmpProjectId()
						.hashCode());
		result = 37 * result
				+ (getPeriodId() == null ? 0 : this.getPeriodId().hashCode());
		return result;
	}
}
