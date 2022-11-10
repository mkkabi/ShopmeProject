package com.shopme.admin.user;

import com.shopme.common.entity.User;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

public class UserCsvExporter {
//    public void export(List<User> userList, HttpServletResponse response) throws IOException {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
//        String date = dateFormat.format(new Date());
//        String filename = "users_"+date+".csv";
//        response.setContentType("text/csv");
//        String headerKey="Content-Disposition";
//        String headerValue = "attachment; filename="+filename;
//        response.setHeader(headerKey, headerValue);
//        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
//        String[] csvHeader = {"User ID", "Email", "First Name", "Last Name", "Roles", "Enabled"};
//        String[] fieldMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"};
//
//        csvBeanWriter.write(csvHeader);
//        for(User user : userList){
//            csvBeanWriter.write(user, fieldMapping);
//        }
//        csvBeanWriter.close();
//    }

    public void export(List<User> users, HttpServletResponse response){

        ICsvBeanWriter beanWriter = null;

        try
        {
            beanWriter = new CsvBeanWriter(new FileWriter("temp.csv"), CsvPreference.STANDARD_PREFERENCE);
            String[] header = {"User ID", "Email", "First Name", "Last Name", "Roles", "Enabled"};
        String[] fieldMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"};
            final CellProcessor[] processors = getProcessors();

            // write the header
            beanWriter.writeHeader(header);

            // write the beans data
            for (User c : users) {
                beanWriter.write(c, fieldMapping);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            try {
                beanWriter.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private static CellProcessor[] getProcessors()
    {
        final String emailRegex = "[a-z0-9\\._]+@[a-z0-9\\.]+";

        StrRegEx.registerMessage(emailRegex, "must be a valid email address");

        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(new ParseInt()), // CustomerId
                new NotNull(), // CustomerName
                new NotNull(), // Country
                new Optional(new ParseLong()), // PinCode
                new StrRegEx(emailRegex) // Email
        };
        return processors;
    }
}
