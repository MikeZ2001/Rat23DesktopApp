package com.example.ratatouille23desktopclient.helpers;
import com.example.ratatouille23desktopclient.caching.RAT23Cache;
import com.example.ratatouille23desktopclient.model.Order;
import com.example.ratatouille23desktopclient.model.OrderItem;
import com.example.ratatouille23desktopclient.model.Store;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CheckGenerator {
    private Order order;
    private Document document;
    private PdfWriter writer;
    private final Font TITLE_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD);
    private final Font STANDARD_FONT = FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL);
    private final Font STANDARD_BOLD = FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD);
    private final Font STANDARD_ITALIC = FontFactory.getFont(FontFactory.TIMES, 10, Font.ITALIC);

    private final String fileName = "check.pdf";


    public CheckGenerator(Order order){
        this.order = order;
        this.document = new Document();
    }

    public File generate() throws Exception{
        startEditing();

        addStoreTitle();
        addStoreInfo();
        addSeparatorLine();
        addItems();
        addSeparatorLine();
        addTotal();
        addSeparatorLine();
        addOtherInfo();
        addThanks();

        stopEditing();
        return new File(fileName);
    }

    private void startEditing() throws Exception{
        writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
        this.document.open();
    }

    private void stopEditing(){
        addAuthor();
        this.document.addCreationDate();

        this.document.close();
        writer.close();
    }

    private void addAuthor(){
        RAT23Cache cache = RAT23Cache.getCacheInstance();
        String name = cache.get("currentUserGivenName").toString();
        String surname = cache.get("currentUserFamilyName").toString();
        this.document.addAuthor(name + " " + surname);
    }

    private void addStoreTitle() throws Exception {
        Store store = order.getTable().getStore();
        Paragraph title = new Paragraph(store.getName(), TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        this.document.add(title);
    }

    private void addStoreInfo() throws Exception{
        Store store = order.getTable().getStore();
        String storeInfos = "";
        if (store.getEmail() != null)
            storeInfos += "\nE-mail: " +store.getEmail();
        if (store.getAddress() != null)
            storeInfos += "\nIndirizzo: " + store.getAddress();
        if (store.getPhone() != null)
            storeInfos += "\nTelefono: " + store.getPhone();

        Paragraph infos = new Paragraph(storeInfos, STANDARD_FONT);
        infos.setAlignment(Element.ALIGN_CENTER);
        this.document.add(infos);
    }

    private void addSeparatorLine() throws Exception{
        String line = "";
        for (int i = 0; i < 86; i++)
            line += "_";
        Paragraph separatorLine = new Paragraph(line, STANDARD_FONT);
        separatorLine.setAlignment(Element.ALIGN_CENTER);
        this.document.add(separatorLine);
    }

    private void addItems() throws Exception {
        PdfPTable table = new PdfPTable(3); // 3 columns.
        table.setWidthPercentage(100); //Width 100%
        table.setSpacingBefore(10f); //Space before table
        table.setSpacingAfter(10f); //Space after table

        float[] columnWidths = {1f, 1f, 1f};
        table.setWidths(columnWidths);

        PdfPCell productHeaderCell = new PdfPCell(new Paragraph("Prodotto", STANDARD_BOLD));
        productHeaderCell.setBorder(Rectangle.NO_BORDER);
        productHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        productHeaderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell quantityHeaderCell = new PdfPCell(new Paragraph("Quantit\u00E0", STANDARD_BOLD));
        quantityHeaderCell.setBorder(Rectangle.NO_BORDER);
        quantityHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        quantityHeaderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell priceHeaderCell = new PdfPCell(new Paragraph("Prezzo", STANDARD_BOLD));
        priceHeaderCell.setBorder(Rectangle.NO_BORDER);
        priceHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        priceHeaderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        table.addCell(productHeaderCell);
        table.addCell(quantityHeaderCell);
        table.addCell(priceHeaderCell);

        for (OrderItem item: order.getItems())
            addItemToTable(table, item);

        this.document.add(table);
    }

    private void addItemToTable(PdfPTable table, OrderItem item){
        PdfPCell productCell = new PdfPCell(new Paragraph(item.getProduct().getName(), STANDARD_FONT));
        productCell.setBorder(Rectangle.NO_BORDER);
        productCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        productCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell quantityCell = new PdfPCell(new Paragraph("x" + item.getQuantity(), STANDARD_FONT));
        quantityCell.setBorder(Rectangle.NO_BORDER);
        quantityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        quantityCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        String subTotal = String.valueOf(item.getQuantity()*item.getProduct().getPrice());
        PdfPCell priceCell = new PdfPCell(new Paragraph(subTotal +"\u20AC", STANDARD_FONT));
        priceCell.setBorder(Rectangle.NO_BORDER);
        priceCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        priceCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        table.addCell(productCell);
        table.addCell(quantityCell);
        table.addCell(priceCell);
    }

    private void addTotal() throws Exception {
        Paragraph total = new Paragraph("TOTALE: " + order.getTotal()+"\u20AC", TITLE_FONT);
        total.setAlignment(Element.ALIGN_CENTER);
        this.document.add(total);
    }

    private void addOtherInfo() throws Exception{
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        Paragraph date = new Paragraph("\nEvaso in data: " + LocalDate.now().format(dateFormatter) + " " + LocalTime.now().format(timeFormatter), STANDARD_ITALIC);
        date.setAlignment(Element.ALIGN_BASELINE);
        this.document.add(date);

        Paragraph idOrder = new Paragraph("ID ordine: " + order.getId(), STANDARD_ITALIC);
        idOrder.setAlignment(Element.ALIGN_BASELINE);
        this.document.add(idOrder);

        Paragraph tablePar = new Paragraph("Tavolo: " + order.getTable().getName(), STANDARD_ITALIC);
        tablePar.setAlignment(Element.ALIGN_BASELINE);
        this.document.add(tablePar);
    }

    private void addThanks() throws Exception {
        Paragraph thanks = new Paragraph("\nGrazie per averci scelto.", STANDARD_FONT);
        thanks.setAlignment(Element.ALIGN_CENTER);
        this.document.add(thanks);
    }
}
