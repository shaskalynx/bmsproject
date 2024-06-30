package com.groupten.bmsproject.JasperReport;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.jar.JarException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.groupten.bmsproject.Admin.Adminentity;
import com.groupten.bmsproject.Admin.Adminrepository;
import com.groupten.bmsproject.Ingredient.IngredientEntity;
import com.groupten.bmsproject.Ingredient.IngredientRepository;
import com.groupten.bmsproject.Sales.SalesEntity;
import com.groupten.bmsproject.Sales.SalesRepository;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;

@Service
public class ReportService {

   @Autowired
   private IngredientRepository ingredientRepository;
   @Autowired
   private SalesRepository salesRepository;


   public String exportReport (String reportFormat) throws FileNotFoundException, JRException{
    String path ="C:\\Users\\Maria\\3rd Year Projects\\softeng\\bmsproject\\src\\main\\java\\com\\groupten\\bmsproject\\Report Files";

    List<IngredientEntity> ingredients = ingredientRepository.findAll();

    //Load file and compile
    File file= ResourceUtils.getFile(("classpath:ReportTemplate/Ingredient_Report.jrxml"));
    JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
    JRBeanCollectionDataSource dataSource= new JRBeanCollectionDataSource(ingredients);

       // Generate a UUID
       String uuid = UUID.randomUUID().toString();

       // Take the first 5 characters as the report ID
       String reportId = uuid.substring(0, 5);

    Map<String,Object> map=new HashMap<>();
    map.put("REPORT_ID", reportId);
    map.put("createdBy","Developer");

    JasperPrint jasperPrint= JasperFillManager.fillReport(jasperReport, map, dataSource);

    if(reportFormat.equalsIgnoreCase("pdf")){
JasperExportManager.exportReportToPdfFile(jasperPrint, path+"\\ingredient_report.pdf");
}
    return "report generated in path:"+path;
   }

   public String exportSalesReport(String reportFormat) throws FileNotFoundException, JRException {
        String path = "C:\\Users\\Maria\\3rd Year Projects\\softeng\\bmsproject\\src\\main\\java\\com\\groupten\\bmsproject\\Report Files";

        List<SalesEntity> sales = salesRepository.findAll();

        // Load file and compile
        File file = ResourceUtils.getFile("classpath:ReportTemplate/Sales_Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(sales);

// Generate a UUID
String uuid = UUID.randomUUID().toString();

// Take the first 5 characters as the report ID
String reportId = uuid.substring(0, 5);

    Map<String,Object> map=new HashMap<>();
    map.put("REPORT_ID", reportId);
    map.put("createdBy","Developer");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, dataSource);

        if (reportFormat.equalsIgnoreCase("pdf")) {
            JasperExportManager.exportReportToPdfFile(jasperPrint, path + "\\sales_report.pdf");
        }
        return "report generated in path:" + path;
    }
}

