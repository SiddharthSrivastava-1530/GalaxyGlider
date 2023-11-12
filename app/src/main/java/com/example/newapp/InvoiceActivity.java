package com.example.newapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.newapp.Activities.CheckoutActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class InvoiceActivity extends AppCompatActivity {
    Button saveAndPrint;
    long invoiceNo = 435873245;
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat datePatternFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
    Bitmap bmp,scaledBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        Objects.requireNonNull(getSupportActionBar()).hide();

        bmp = BitmapFactory.decodeResource(getResources(),R.drawable.bg);
        scaledBitmap = Bitmap.createScaledBitmap(bmp,250,60,false);
        callFindViewById();
        callOnClickListener();
    }





    private void callOnClickListener() {
        saveAndPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                printPdf();
                Toast.makeText(InvoiceActivity.this, "Invoice Generated and downloaded", Toast.LENGTH_SHORT).show();

            }
        });
    }




    private void printPdf() {
        int y;
        PdfDocument myPdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint linePaint = new Paint();
        linePaint.setColor(Color.rgb(0,0,0));
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(250,350,1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
        Canvas canvas = myPage.getCanvas();

        canvas.drawBitmap(scaledBitmap,0,0,paint);

        paint.setTextSize(18f);
        paint.setTypeface(Typeface.SERIF);
        paint.setColor(Color.rgb(227,240,237));
        canvas.drawText("Welcome to Galaxy Glider",12,35,paint);

        paint.setTextSize(8.5f);
        linePaint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(0,0,0));

        //  Details
        y = 75;
        canvas.drawText("Invoice No :  ",20,y,paint);
        canvas.drawText("Date & Time :  ",20,y+12,paint);
        canvas.drawText("Status :  ",20,y+24,paint);
        y+=36;
        canvas.drawText("Customer Name :  ",20,y,paint);
        canvas.drawText("Email :  ",20,y+12,paint);
        canvas.drawText("From :  ",20,y+24,paint);
        canvas.drawText("To :  ",20,y+36,paint);
        canvas.drawText("Total Distance :  ",20,y+48,paint);
        canvas.drawText("Company :  ",20,y+60,paint);
        canvas.drawText("SpaceShip :  ",20,y+72,paint);


        // get details from database/ intent
        String name = "Nishu Sharma";
        String mail = "as.nishu18@gmail.com";
        String from = "Earth";
        String to = "Jupiter";
        long dis = 3768749749L;
        String company = "SpaceX";
        String spaceShip = "Luna";


        y = 75;
        canvas.drawText(String.valueOf(invoiceNo+1),140,y,paint);
        canvas.drawText(datePatternFormat.format(new Date().getTime()),140,y+12,paint);
        canvas.drawText("Paid",140,y+24,paint);
        y+=36;
        canvas.drawText(name,140,y,paint);
        canvas.drawText(mail,140,y+12,paint);
        canvas.drawText(from,140,y+24,paint);
        canvas.drawText(to,140,y+36,paint);
        canvas.drawText(String.valueOf(dis)+" LightYears",140,y+48,paint);
        canvas.drawText(company,140,y+60,paint);
        canvas.drawText(spaceShip,140,y+72,paint);

        y += 82;
        canvas.drawLine(10,y,240,y,linePaint);
        paint.setTextSize(13f);
        canvas.drawText("Fair Calculation :" ,20,y+20,paint);
        paint.setTextSize(8.5f);
        y +=40;

        // amount calculation ---- Dynamic Fair
        double amount = 1248f;

        canvas.drawText("Basic Pay " ,20,y,paint);
        canvas.drawText("20rs/LightYear",120,y,paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.valueOf(decimalFormat.format(amount)),230,y,paint);
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawText("Service Charges" ,20,y+12,paint);
        canvas.drawText("Tax 18%",120,y+12,paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(decimalFormat.format(amount*18/100),230,y+12,paint);
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawText("Additional Charges" ,20,y+24,paint);
        canvas.drawText("Space Tax 4%",120,y+24,paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(decimalFormat.format(amount*4/100),230,y+24,paint);
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawText("Trafic Cost" ,20,y+36,paint);
        canvas.drawText("Variable",120,y+36,paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(decimalFormat.format(amount*2/100),230,y+36,paint);
        paint.setTextAlign(Paint.Align.LEFT);

        double totalAmount = amount+amount*18/100 +amount*4/100+ amount*2/100 ;
        canvas.drawText("Total" ,20,y+53,paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(decimalFormat.format(totalAmount),230,y+53,paint);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawLine(10,y+63,240,y+63,linePaint);

        //TOTAL
        y +=63;
        paint.setTextSize(12f);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Amount Paid :  "+decimalFormat.format(totalAmount),230,y+15,paint);
        paint.setTextAlign(Paint.Align.LEFT);


        // creating file
        myPdfDocument.finishPage(myPage);
        String pdfName = spaceShip+name+".pdf";
        File file = new File(this.getExternalFilesDir("/"),pdfName);
        try {
            myPdfDocument.writeTo(new FileOutputStream(file));
            // print invoice in new activity
            Intent intent = new Intent(InvoiceActivity.this, CheckoutActivity.class);
            startActivity(intent);


        } catch (IOException e) {
            e.printStackTrace();
        }
        myPdfDocument.close();

    }

    private void callFindViewById() {
        saveAndPrint = findViewById(R.id.GenerateInvoice);
    }

}