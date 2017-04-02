package com.kstrata.apps.tsm.business.uibeans;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.BeanUtils;

import com.kstrata.apps.tsm.business.dao.entity.Client;
import com.kstrata.apps.tsm.business.dao.entity.Employee;
import com.kstrata.apps.tsm.business.dao.entity.EmployeeProject;
import com.kstrata.apps.tsm.business.dao.entity.Period;
import com.kstrata.apps.tsm.business.dao.entity.Project;
import com.kstrata.apps.tsm.business.dao.entity.Timesheet;
import com.kstrata.apps.tsm.business.model.JRTimesheet;
import com.kstrata.apps.tsm.business.service.ClientService;
import com.kstrata.apps.tsm.business.service.EmployeeProjectService;
import com.kstrata.apps.tsm.business.service.EmployeeService;
import com.kstrata.apps.tsm.business.service.PeriodService;
import com.kstrata.apps.tsm.business.service.ProjectService;
import com.kstrata.apps.tsm.business.service.TimesheetService;
import com.kstrata.apps.tsm.business.util.TimesheetComparator;

@ManagedBean(name = "reportBean")
@ViewScoped
public class ReportBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Resource
	private TimesheetService timesheetService;
	@Resource
	private EmployeeProjectService employeeProjectService;
	@Resource
	private PeriodService periodService;
	@Resource
	private EmployeeService employeeService;
	@Resource
	private ProjectService projectService;
	@Resource
	private ClientService clientService;

	private Set<SelectItem> clientNames;
	private String clientName;
	private String shortName;
	private Integer clientId;
	private Date startDate;
	private Date endDate;
	private Employee employee;
	private String employeeName;
	private Map<Integer, Employee> employeeMap = new HashMap<Integer, Employee>();
	private Period currentPeriod;
	private boolean errorsPresent;
	private String errorMessage;
	private List<Timesheet> timesheetList;
	List<JasperPrint> jasperPrintList = null;
	Map<String, List<Timesheet>> mapList;
	private boolean generateRegularReport = true;
	private boolean generateOvertimeReport = true;

	@PostConstruct
	public void init() {
		prepareClientNames();
		prepareEmployees();
		prepareCurrentPeriod();
		prepareDates();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void generateReport1() {
		validateClientName();
		if (!errorsPresent) {
			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext externalContext = fc.getExternalContext();
			String path = externalContext
					.getRealPath("/jasper/timesheet.jasper");
			String tempPath = externalContext.getRealPath("/temp");

			HttpServletResponse response = (HttpServletResponse) externalContext
					.getResponse();
			jasperPrintList = new ArrayList();
			List<Integer> projectIds = prepareProjectIds();
			int i = 0;
			String[] empNames = null;
			if (employee != null) {

				empNames = new String[1];
				List<Integer> employeeProjectIds = prepareEmployeeProjectsForEmployee(employee, projectIds);
				if (employeeProjectIds.size() != 0) {
					List<Timesheet> timesheets = timesheetService.getTimesheetsByDateRange(employeeProjectIds,
									startDate, endDate);
					// System.out.println(timesheets.size());
					if (timesheets.size() != 0) {
						try {
							empNames[i] = (employee.getEmpFirstName() + " " + employee
									.getEmpLastName());
							i++;
							clientName = timesheets.get(0).getEmployeeProject()
									.getProject().getClient().getClientName();
							shortName = timesheets.get(0).getEmployeeProject()
									.getProject().getClient().getShortName();
							prepareJPAndAddToJPList(new JRBeanArrayDataSource(
									convertToJRTimesheetRows(timesheets)
											.toArray()), path);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			} else {
				mapList = new HashMap<String, List<Timesheet>>();
				empNames = new String[employeeMap.entrySet().size()];
				Iterator iterator = employeeMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry employee = (Map.Entry) iterator.next();
					Employee currentEmployee = (Employee) employee.getValue();
					List<Integer> employeeProjectIds = prepareEmployeeProjectsForEmployee(
							currentEmployee, projectIds);
					if (employeeProjectIds.size() != 0) {
						List<Timesheet> timesheets = timesheetService
								.getTimesheetsByDateRange(employeeProjectIds,
										startDate, endDate);
						// System.out.println(timesheets.size());
						if (timesheets.size() != 0) {
							try {
								empNames[i] = (currentEmployee
										.getEmpFirstName() + " " + currentEmployee
										.getEmpLastName());
								i++;
								clientName = timesheets.get(0)
										.getEmployeeProject().getProject()
										.getClient().getClientName();
								shortName = timesheets.get(0)
										.getEmployeeProject().getProject()
										.getClient().getShortName();
								prepareJPAndAddToJPList(
										new JRBeanArrayDataSource(
												convertToJRTimesheetRows(
														timesheets).toArray()),
										path);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
				}
			}

			try {
				if (null == jasperPrintList || jasperPrintList.isEmpty()) {
					setErrorMessage("No Timesheets for selected period");
					setErrorsPresent(true);
					return;
				}
				exportToXLS(empNames, tempPath, response, fc);
			} catch (JRException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public void generateReport() {
		setErrorsPresent(false);
		validateClientName();
		if (!(generateOvertimeReport || generateRegularReport)) {
			setErrorMessage("Please Choose Regular / Overtime");
			setErrorsPresent(true);
			return;
		}
		if (!errorsPresent) {
			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext externalContext = fc.getExternalContext();
			String timesheetPath = externalContext.getRealPath("/reports/reportTemplate.xlsx");
		    String tempPath = externalContext.getRealPath("/temp");
			String overtimePath = externalContext.getRealPath("/reports/overtimeTemplate.xlsx");
			File files = new File(tempPath);
			for (File f : files.listFiles()) {
				f.delete();
			}

			HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
			timesheetList = new ArrayList<Timesheet>();
			List<Integer> projectIds = prepareProjectIds();
			int i = 0;
			String[] empNames = null;
			if (employee != null) {
				mapList = new HashMap<String, List<Timesheet>>();
				empNames = new String[1];
				List<Integer> employeeProjectIds = prepareEmployeeProjectsForEmployee(employee, projectIds);
				if (employeeProjectIds.size() != 0) {
					timesheetList = timesheetService.getTimesheetsByDateRange(employeeProjectIds, startDate, endDate);
					if (timesheetList.size() != 0) {
						try {
							empNames[i] = (employee.getEmpFirstName() + " " + employee.getEmpLastName());
							employeeName = empNames[i];
							i++;
							clientName = timesheetList.get(0).getEmployeeProject().getProject().getClient().getClientName();
							shortName = timesheetList.get(0).getEmployeeProject().getProject().getClient().getShortName();
							mapList.put(employeeName, timesheetList);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				mapList = new HashMap<String, List<Timesheet>>();
				empNames = new String[employeeMap.entrySet().size()];
				Iterator iterator = employeeMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry employee = (Map.Entry) iterator.next();
					Employee currentEmployee = (Employee) employee.getValue();
					List<Integer> employeeProjectIds = prepareEmployeeProjectsForEmployee(currentEmployee, projectIds);
					if (employeeProjectIds.size() != 0) {
						List<Timesheet> timeSheetList = timesheetService.getTimesheetsByDateRange(employeeProjectIds, startDate, endDate);
						if (timeSheetList.size() != 0) {
							try {
								empNames[i] = (currentEmployee.getEmpFirstName() + " " + currentEmployee.getEmpLastName());
								employeeName = empNames[i];
								i++;
								clientName = timeSheetList.get(0).getEmployeeProject().getProject().getClient().getClientName();
								shortName = timeSheetList.get(0).getEmployeeProject().getProject().getClient().getShortName();
								timesheetList.addAll(timeSheetList);
								mapList.put(employeeName, timeSheetList);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}

			if (null == timesheetList || timesheetList.isEmpty()) {
				setErrorMessage("No Timesheets for selected period");
				setErrorsPresent(true);
				return;
			}

			for (String key : mapList.keySet()) {
				List<Timesheet> sheets = mapList.get(key);
				Map<String, List<Timesheet>> taskTypeMap = new TreeMap<String, List<Timesheet>>();

				for (Timesheet timesheet : sheets) {
					String type = timesheet.getTaskType();
					if (!taskTypeMap.containsKey(type)) {
						taskTypeMap.put(type, new ArrayList<Timesheet>());
					}
					taskTypeMap.get(type).add(timesheet);
				}

				for (String taskType : taskTypeMap.keySet()) {
					List<Timesheet> timesheetList1 = taskTypeMap.get(taskType);
					if (taskType.equalsIgnoreCase("RT") && generateRegularReport) {
						createXLSX(key, timesheetPath, tempPath, response, fc, timesheetList1, taskType);
					}
					if (taskType.equalsIgnoreCase("OT") && generateOvertimeReport) {
						createXLSX(key, overtimePath, tempPath, response, fc, timesheetList1, taskType);
					}
				}
			}
			exportToZip(empNames, tempPath, response, fc);
		}
	}

	public void createXLSX(String empName, String path, String tempPath,
			HttpServletResponse response, FacesContext fc, List<Timesheet> timesheetList, String taskType) {
		try {
			if (empName != null) {
				InputStream inputStream = new FileInputStream(path);
				Workbook workbook = WorkbookFactory.create(inputStream);
				Sheet worksheet = workbook.getSheetAt(0);

				Row row1 = worksheet.getRow(1);
				Cell cellE1 = row1.getCell(4);
				cellE1.setCellValue(empName);
				Row row2 = worksheet.getRow(2);
				Cell cellE2 = row2.getCell(4);
				cellE2.setCellValue(timesheetList.get(0).getEmployeeProject().getEmployee().getEmpDesignation());
				Row row3 = worksheet.getRow(3);
				Cell cellE3 = row3.getCell(4);
				cellE3.setCellValue(clientName);
				// date
				Cell cellH1 = row1.getCell(7);
				cellH1.setCellValue(formatDate(startDate));
				Cell cellH2 = row2.getCell(7);
				cellH2.setCellValue(formatDate(endDate));

				Map<Date, List<Timesheet>> map = new TreeMap<Date, List<Timesheet>>();
				for (Timesheet timesheet : timesheetList) {
					Date key = timesheet.getTaskDate();
					if (!map.containsKey(key)) {
						map.put(key, new ArrayList<Timesheet>());
					}
					map.get(key).add(timesheet);
				}

				int row = 6;

				BigDecimal timesheetTotal = new BigDecimal(0.00);
				for (Date date : map.keySet()) {

					List<Timesheet> timesheetList1 = map.get(date);
					int size = timesheetList1.size();
					int mergeRowRange = size - 1;
					worksheet.addMergedRegion(new CellRangeAddress(row, row + mergeRowRange, 2, 2));
					/*if (taskType.equalsIgnoreCase("RT")) {
						worksheet.addMergedRegion(new CellRangeAddress(row, row + mergeRowRange, 8, 8));
					} else
						worksheet.addMergedRegion(new CellRangeAddress(row, row + mergeRowRange, 7, 7));*/

					Row currentRow = null;
					int cellRange = 0;
					int lastCell = 0;
					BigDecimal total = new BigDecimal(0.00);
					for (Timesheet timesheet : timesheetList1) {
						cellRange = 2;
						currentRow = worksheet.createRow(row);

						Cell currentCell = currentRow.createCell(cellRange++);
						currentCell.setCellValue(formatDate(timesheet.getTaskDate()));
						setCenterCellStyle(currentCell, workbook);

						currentCell = currentRow.createCell(cellRange++);
						currentCell.setCellValue(timesheet.getEmployeeProject().getProject().getProjectName());
						setTopCellStyle(currentCell, workbook);

						String taskDes = "";

						for (int i = 0; i < timesheet.getTaskDesc().length; i++) {
							taskDes += (char) timesheet.getTaskDesc()[i];
						}
						String comments = "";

						for (int i = 0; i < timesheet.getComments().length; i++) {
							comments += (char) timesheet.getComments()[i];
						}

						currentCell = currentRow.createCell(cellRange++);
						currentCell.setCellValue(taskDes);
						setTopCellStyle(currentCell, workbook);

						if (taskType.equalsIgnoreCase("RT")) {
							currentCell = currentRow.createCell(cellRange++);
							currentCell.setCellValue(comments);
							setTopCellStyle(currentCell, workbook);
						}

						currentCell = currentRow.createCell(cellRange++);
						currentCell.setCellValue(formatDateShowingOnlyTime(timesheet.getStartTime()));
						setTopCenterCellStyle(currentCell, workbook);

						currentCell = currentRow.createCell(cellRange++);
						currentCell.setCellValue(formatDateShowingOnlyTime(timesheet.getEndTime()));
						setTopCenterCellStyle(currentCell, workbook);

						/*Calendar start = Calendar.getInstance();
						Calendar end = Calendar.getInstance();
						start.setTime(timesheet.getStartTime());
						end.setTime(timesheet.getEndTime());*/
						//timeDiff += (end.getTimeInMillis() - start.getTimeInMillis());
						//total = prepareTotal(timesheet.getTotal(), total);
						total = prepareTotal(timesheet.getTotal());

						lastCell = cellRange++;
						currentCell = currentRow.createCell(lastCell);
						currentCell.setCellValue(total.doubleValue());
						setCenterCellStyle(currentCell, workbook);
						row++;
						
						timesheetTotal = timesheetTotal.add(total);
					}
					
					/*currentRow = worksheet.getRow(row - size);
					Cell totalValue = currentRow.getCell(lastCell);
					totalValue.setCellValue(total.doubleValue());*/
				}
				
				row = row + 1;
				Row currentRow = worksheet.createRow(row);
				Cell currentCell = currentRow.createCell(7);
				currentCell.setCellValue("Total");
				
				currentCell = currentRow.createCell(8);
				currentCell.setCellValue(getNearestInteger(timesheetTotal.doubleValue()));
				
				/*row = row + 1;
				currentRow = worksheet.createRow(row);
				currentCell = currentRow.createCell(7);
				currentCell.setCellValue("Rounded To");
				
				currentCell = currentRow.createCell(8);
				currentCell.setCellValue(getRoundedValue(timesheetTotal.doubleValue()));*/

				File file = null;
				if (taskType.equalsIgnoreCase("RT")) {

					file = new File(tempPath + "/" + empName + " FROM "
							+ formatDate(startDate) + "_TO_"
							+ formatDate(endDate) + "("
							+ clientName.toUpperCase() + ").xlsx");
					if (file.exists()) {
						file.delete();
					} else
						file.createNewFile();
				} else {
					file = new File(tempPath + "/" + empName
							+ "_Overtime_FROM " + formatDate(startDate)
							+ "_TO_" + formatDate(endDate) + "("
							+ clientName.toUpperCase() + ").xlsx");
					if (file.exists()) {
						file.delete();
					} else
						file.createNewFile();
				}

				FileOutputStream fileOutputStream = new FileOutputStream(file);
				workbook.write(fileOutputStream);
				fileOutputStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private double getRoundedValue(double doubleValue) {
		return Math.round(doubleValue / .5) * .5;
	}
	
	private double getNearestInteger(double doubleValue) {
		return Math.round(doubleValue);
	}
	
	public static void main(String[] args) {
		ReportBean bean = new ReportBean();
		System.out.println(bean.getRoundedValue(8.38));
		System.out.println(Math.round(8.5));
		//System.out.println(bean.prepareTotal(new BigDecimal(.13)));
	}

	@SuppressWarnings("unused")
	private BigDecimal prepareTotal(BigDecimal totalFromDB, BigDecimal totalTemp) {
		double hours = totalFromDB.intValue() + totalTemp.intValue();
		double mins = getMinutesFromTotal(totalFromDB) + getMinutesFromTotal(totalTemp);
		return BigDecimal.valueOf(hours + convertMinsToHours(mins).doubleValue());
	}
	
	private BigDecimal prepareTotal(BigDecimal totalFromDB) {
		double hours = totalFromDB.intValue();
		double mins = getMinutesFromTotal(totalFromDB);
		
		Double finalHours = (hours * 60 + mins) / 60;
		
		return BigDecimal.valueOf(new Double(new DecimalFormat("#.##").format(finalHours)));
	}
	
	private double getMinutesFromTotal(BigDecimal totalTemp) {
		String[] s = totalTemp.toPlainString().split("\\.");
		if (s.length>1) {
			return Double.parseDouble(s[1].substring(0, Math.min(s[1].length(), 2)));
		} else {
			return 0.0;
		}
	}
	
	private BigDecimal convertMinsToHours(Double mins) {
		if (mins<60) {
			return new BigDecimal("." + mins.intValue());
		} else {
			Double qoutient = mins / 60;
			Double remainder = mins % 60;
			return new BigDecimal(qoutient.intValue() + "." + 
					remainder.toString().substring(0, Math.min(remainder.toString().length(), 2)));
		}
	}
	
	public CellStyle setDefaultCellStyle(Cell cell, Workbook workbook) {

		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(CellStyle.BORDER_THICK);
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);
		cellStyle.setBorderTop(CellStyle.BORDER_THIN);
		return cellStyle;
	}

	public void setTopCellStyle(Cell cell, Workbook workbook) {
		CellStyle cellStyle = setDefaultCellStyle(cell, workbook);
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		cell.setCellStyle(cellStyle);
	}
	public void setTopCenterCellStyle(Cell cell, Workbook workbook) {
		CellStyle cellStyle = setDefaultCellStyle(cell, workbook);
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cell.setCellStyle(cellStyle);
	}
	public void setCenterCellStyle(Cell cell, Workbook workbook) {
		CellStyle cellStyle = setDefaultCellStyle(cell, workbook);
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cell.setCellStyle(cellStyle);
	}

	public void exportToZip(String[] empName, String tempPath, HttpServletResponse response, FacesContext fc) {
		ZipOutputStream zipOut = null;
		try {
			response.setHeader("Content-disposition", "attachement; filename= "
					+ shortName + "-" + formatDateForZip(startDate) + "-"
					+ formatDateForZip(endDate) + ".zip");
			response.setContentType("application/zip");
			zipOut = new ZipOutputStream(response.getOutputStream());
			File file = new File(tempPath);

			for (File f : file.listFiles()) {
				zipOut.putNextEntry(new ZipEntry(f.getName()));

				zipOut.write(FileUtils.readFileToByteArray(f));
			}

			zipOut.closeEntry();
			zipOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fc.responseComplete();
		}

	}

	@SuppressWarnings("unused")
	private void exportToXLSX(String[] empNames, String tempPath,
			HttpServletResponse response, FacesContext fc) throws JRException,
			IOException {
		ZipOutputStream zipOut = null;
		try {
			response.setHeader("Content-disposition", "attachement; filename= "
					+ shortName + "-" + formatDateForZip(startDate) + "-"
					+ formatDateForZip(endDate) + ".zip");
			response.setContentType("application/zip");
			zipOut = new ZipOutputStream(response.getOutputStream());

			File file = new File(tempPath + "/" + formatDate(new Date()) + "_"
					+ (empNames[0]) + ".xlsx");
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			FileOutputStream fileOutputStream = new FileOutputStream(file);
			zipOut.putNextEntry(new ZipEntry((empNames[0]) + " FROM "
					+ formatDate(startDate) + "_TO_" + formatDate(endDate)
					+ "(" + clientName.toUpperCase() + ").xlsx"));

			fileOutputStream.close();

			zipOut.write(FileUtils.readFileToByteArray(file));
			zipOut.closeEntry();
			file.delete();

			fc.responseComplete();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception jEx) {
			jEx.printStackTrace();
		} finally {
			try {
				// Flush the stream
				zipOut.flush();
			} catch (Exception e) {
			}

			try {
				// Close the stream
				zipOut.close();
			} catch (Exception e) {

			}
		}
	}

	private List<JRTimesheet> convertToJRTimesheetRows(
			List<Timesheet> timesheets) {
		List<JRTimesheet> jrTimeSheetList = new ArrayList<JRTimesheet>();
		Map<String, List<JRTimesheet>> map = new TreeMap<String, List<JRTimesheet>>();
		long timeDiff = 0L;
		for (Timesheet timesheet : timesheets) {
			String key = getMapKey(timesheet.getTaskDate());
			JRTimesheet jrTimesheet = new JRTimesheet();
			BeanUtils.copyProperties(timesheet, jrTimesheet);
			jrTimesheet.setDateFromString(formatDate(startDate));
			jrTimesheet.setDateToString(formatDate(endDate));
			jrTimesheet.setTaskDateString(formatDate(timesheet.getTaskDate()));

			jrTimesheet.setStartTimeString(formatDateShowingOnlyTime(timesheet
					.getStartTime()));
			jrTimesheet.setEndTimeString(formatDateShowingOnlyTime(timesheet
					.getEndTime()));

			jrTimesheet.setClientName(timesheet.getEmployeeProject()
					.getProject().getClient().getClientName());
			jrTimesheet.setProjectName(timesheet.getEmployeeProject()
					.getProject().getProjectName());
			jrTimesheet.setDesignation(timesheet.getEmployeeProject()
					.getEmployee().getEmpDesignation());
			jrTimesheet.setEmpFirstName(timesheet.getEmployeeProject()
					.getEmployee().getEmpFirstName());
			jrTimesheet.setEmpLastName(timesheet.getEmployeeProject()
					.getEmployee().getEmpLastName());
			jrTimesheet.setEmployeeRef(timesheet.getEmployeeProject()
					.getEmployee().getEmployeeRef());
			jrTimesheet.setShowClient(true);

			if (!map.containsKey(key)) {
				map.put(key, new ArrayList<JRTimesheet>());
				map.get(key).add(jrTimesheet);
			} else {
				timeDiff = 0L;
				List<JRTimesheet> jrTimesheets = map.get(key);
				Calendar start = Calendar.getInstance();
				Calendar end = Calendar.getInstance();
				jrTimesheets.add(jrTimesheet);
				for (JRTimesheet jrt : jrTimesheets) {
					start.setTime(jrt.getStartTime());
					end.setTime(jrt.getEndTime());
					timeDiff += (end.getTimeInMillis() - start
							.getTimeInMillis());
				}
				jrTimesheets.get(0).setTotal(calTotal(timeDiff));

			}
		}
		for (String taskDate : map.keySet()) {
			List<JRTimesheet> jrTimesheets = map.get(taskDate);
			int i = 0;
			for (JRTimesheet jrTimesheet : jrTimesheets) {
				if (i > 0) {
					jrTimesheet.setTotal(null);
				}
				i++;
				jrTimeSheetList.add(jrTimesheet);
			}
		}
		Collections.sort(jrTimeSheetList, new TimesheetComparator());
		return jrTimeSheetList;
	}

	private String getMapKey(Date taskDate) {
		String key = "";
		try {
			DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
			key = dateFormat.format(taskDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return key;
	}

	private BigDecimal calTotal(long timeDiff) {
		Long diffHours = new Long((timeDiff) / (60 * 60 * 1000));
		Long diffMins = new Long((timeDiff) / (60 * 1000));
		if (Math.abs(diffMins % 60) < 10)
			return new BigDecimal(Math.abs(diffHours) + ".0"
					+ Math.abs(diffMins % 60));
		else
			return new BigDecimal(Math.abs(diffHours) + "."
					+ Math.abs(diffMins % 60));
	}

	private String formatDateShowingOnlyTime(Date date) {
		String s = null;
		try {
			DateFormat df = new SimpleDateFormat("kk:mm");
			s = df.format(date);
		} catch (Exception e) {

		}
		return s;
	}

	private String formatDate(Date date) {
		String s = null;
		try {
			DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
			s = df.format(date);
		} catch (Exception e) {

		}
		return s;
	}

	private String formatDateForZip(Date date) {
		String s = null;
		try {
			DateFormat df = new SimpleDateFormat("ddMMyy");
			s = df.format(date);
		} catch (Exception e) {

		}
		return s;
	}

	private void prepareJPAndAddToJPList(JRBeanArrayDataSource dataSource,
			String path) throws Exception {
		JasperPrint jasperPrint = JasperFillManager.fillReport(
				new BufferedInputStream(new FileInputStream(new File(path))),
				null, dataSource);
		// populate into list
		jasperPrintList.add(jasperPrint);
	}

	private void exportToXLS(String[] empNames, String tempPath,
			HttpServletResponse response, FacesContext fc) throws JRException,
			IOException {
		ZipOutputStream zipOut = null;
		try {
			// Add the response headers
			response.setHeader("Content-disposition", "attachement; filename= "
					+ shortName + "-" + formatDateForZip(startDate) + "-"
					+ formatDateForZip(endDate) + ".zip");
			response.setContentType("application/zip");
			zipOut = new ZipOutputStream(response.getOutputStream());
			if (jasperPrintList != null && jasperPrintList.size() > 0) {
				int i = 0;
				for (JasperPrint jasperPrint : jasperPrintList) {
					File file = new File(tempPath + "/"
							+ formatDate(new Date()) + "_" + (empNames[i])
							+ ".xls");
					if (file.exists()) {
						file.delete();
					}
					file.createNewFile();

					FileOutputStream fileOutputStream = new FileOutputStream(
							file);
					zipOut.putNextEntry(new ZipEntry((empNames[i]) + " FROM "
							+ formatDate(startDate) + "_TO_"
							+ formatDate(endDate) + "("
							+ clientName.toUpperCase() + ").xls"));
					i++;
					JRXlsExporter exporter = new JRXlsExporter();
					exporter.setParameter(JRExporterParameter.JASPER_PRINT,
							jasperPrint);
					exporter.setParameter(
							JRXlsAbstractExporterParameter.OUTPUT_STREAM,
							fileOutputStream);
					exporter.setParameter(
							JRXlsAbstractExporterParameter.IS_COLLAPSE_ROW_SPAN,
							Boolean.TRUE);
					exporter.setParameter(
							JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS,
							Boolean.TRUE);
					exporter.setParameter(
							JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
							Boolean.TRUE);
					exporter.setParameter(
							JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET,
							Boolean.FALSE);
					exporter.setParameter(
							JRXlsAbstractExporterParameter.IS_DETECT_CELL_TYPE,
							Boolean.TRUE);
					/*
					 * exporter.setParameter(JRXlsAbstractExporterParameter.
					 * IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
					 * exporter.setParameter
					 * (JRXlsAbstractExporterParameter.IS_IGNORE_GRAPHICS,
					 * Boolean.TRUE);
					 */
					exporter.exportReport();
					fileOutputStream.close();

					zipOut.write(FileUtils.readFileToByteArray(file));
					zipOut.closeEntry();
					file.delete();
				}
				fc.responseComplete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JRException jEx) {
			jEx.printStackTrace();
		} catch (Exception jEx) {
			jEx.printStackTrace();
		} finally {
			try {
				// Flush the stream
				zipOut.flush();
			} catch (Exception e) {
			}

			try {
				// Close the stream
				zipOut.close();
			} catch (Exception e) {
			}
		}

	}

	private List<Integer> prepareProjectIds() {
		List<Project> projects = projectService
				.getActiveProjectsByClient(clientId);
		List<Integer> projectIds = new ArrayList<Integer>();
		for (Project project : projects) {
			projectIds.add(project.getProjectId());
		}
		return projectIds;
	}

	private List<Integer> prepareEmployeeProjectsForEmployee(Employee employee,
			List<Integer> projectIds) {
		List<EmployeeProject> employeeProjects = employeeProjectService.findEmployeeProjectByEmployee(employee, projectIds);
		List<Integer> employeeProjectIds = new ArrayList<Integer>();
		for (EmployeeProject employeeProject : employeeProjects) {
			employeeProjectIds.add(employeeProject.getEmpProjId());
		}
		return employeeProjectIds;
	}

	private void validateClientName() {
		if (clientId == 0) {
			setErrorMessage("Please Select Client");
			setErrorsPresent(true);
		} else
			setErrorsPresent(false);
	}

	@SuppressWarnings("deprecation")
	private void prepareCurrentPeriod() {
		Date currentDate = new Date();
		currentDate.setHours(0);
		currentDate.setMinutes(0);
		currentDate.setSeconds(0);
		this.currentPeriod = periodService.getPeriod(currentDate);
	}

	@SuppressWarnings("unchecked")
	private void prepareClientNames() {
		List<Client> clients = new ArrayList<Client>();
		clients = clientService.findAll();
		clientNames = new HashSet<SelectItem>();
		for (Client client : clients) {
			clientNames.add(new SelectItem(client.getClientId(), client
					.getClientName()));
		}
		clientName = "";
		clientId = 0;
		clientNames.add(new SelectItem(0, clientName));
	}

	private void prepareEmployees() {
		List<Employee> employees = new ArrayList<Employee>();
		employees = employeeService.getActiveEmployees();
		for (Employee employee : employees) {
			employeeMap.put(employee.getEmpId(), employee);
		}
	}

	private void prepareDates() {
		startDate = currentPeriod.getDateFrom();
		endDate = currentPeriod.getDateTo();
	}

	public void reset() {
		setEmployee(null);
		employeeName = "";
		clientId = 0;
		clientName = "";
		prepareDates();
		setErrorsPresent(false);
	}

	public void fetchEmployee(Long waste) {
		if (employeeName != "") {
			setEmployee(employeeMap.get(Integer.parseInt(employeeName.split("-")[1].trim())));
		} else
			setEmployee(null);
	}

	public List<Employee> autocomplete(final Object suggestion) {
		return employeeService.getEmployees(suggestion.toString());
	}

	protected void finalize() throws Throwable {
		employeeMap = null;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public Set<SelectItem> getClientNames() {
		return clientNames;
	}

	public void setClientNames(Set<SelectItem> clientNames) {
		this.clientNames = clientNames;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Period getCurrentPeriod() {
		return currentPeriod;
	}

	public void setCurrentPeriod(Period currentPeriod) {
		this.currentPeriod = currentPeriod;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Map<Integer, Employee> getEmployeeMap() {
		return employeeMap;
	}

	public void setEmployeeMap(Map<Integer, Employee> employeeMap) {
		this.employeeMap = employeeMap;
	}

	public boolean isErrorsPresent() {
		return errorsPresent;
	}

	public void setErrorsPresent(boolean errorsPresent) {
		this.errorsPresent = errorsPresent;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public TimesheetService getTimesheetService() {
		return timesheetService;
	}

	public void setTimesheetService(TimesheetService timesheetService) {
		this.timesheetService = timesheetService;
	}

	public EmployeeProjectService getEmployeeProjectService() {
		return employeeProjectService;
	}

	public void setEmployeeProjectService(
			EmployeeProjectService employeeProjectService) {
		this.employeeProjectService = employeeProjectService;
	}

	public PeriodService getPeriodService() {
		return periodService;
	}

	public void setPeriodService(PeriodService periodService) {
		this.periodService = periodService;
	}

	public EmployeeService getEmployeeService() {
		return employeeService;
	}

	public void setEmployeeService(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	public ProjectService getProjectService() {
		return projectService;
	}

	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}

	public ClientService getClientService() {
		return clientService;
	}

	public void setClientService(ClientService clientService) {
		this.clientService = clientService;
	}

	public boolean isGenerateRegularReport() {
		return generateRegularReport;
	}

	public void setGenerateRegularReport(boolean generateRegularReport) {
		this.generateRegularReport = generateRegularReport;
	}

	public boolean isGenerateOvertimeReport() {
		return generateOvertimeReport;
	}

	public void setGenerateOvertimeReport(boolean generateOvertimeReport) {
		this.generateOvertimeReport = generateOvertimeReport;
	}

}
