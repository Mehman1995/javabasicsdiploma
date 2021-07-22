package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;


public class Convert implements TextGraphicsConverter {
    private int width;
    private int height;
    private double maxRatio;
    private TextColorSchema schema;

    //цветовая схема по умолчанию
    public Convert() {
        schema = new Chariki();
    }

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));

        int newWidth = 0;
        int newHeight = 0;
        double ratio = 0;
        double koefW = 0;
        double koefH = 0;

        //макс доступ сторон
        if (img.getWidth() / img.getHeight() > img.getHeight() / img.getWidth()) {
            ratio = (double) img.getWidth() / (double) img.getHeight();
        } else {
            ratio = (double) img.getHeight() / (double) img.getWidth();
        }
        //Ошибка
        if (ratio > maxRatio && maxRatio != 0) throw new BadImageSizeException(ratio, maxRatio);

        //установка макс. ширины/длинны картинки
        if (img.getWidth() > width || img.getHeight() > height) {
            //коэффициенты сжатия картинки
            if (width != 0) {
                koefW = img.getWidth() / width;
            } else koefW = 1;
            if (height != 0) {
                koefH = img.getHeight() / height;
            } else koefH = 1;

            if (koefW > koefH) {
                newWidth = (int) (img.getWidth() / koefW);
                newHeight = (int) (img.getHeight() / koefW);
            } else {
                newWidth = (int) (img.getWidth() / koefH);
                newHeight = (int) (img.getHeight() / koefH);
            }
        } else {
            newWidth = img.getWidth();
            newHeight = img.getHeight();
        }

        char[][] graph = new char[newHeight][newWidth];

        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);

        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D graphics = bwImg.createGraphics();

        graphics.drawImage(scaledImage, 0, 0, null);

        var bwRaster = bwImg.getRaster();

        for (int h = 0; h < newHeight; h++) {
            for (int w = 0; w < newWidth; w++) {
                int color = bwRaster.getPixel(w, h, new int[3])[0];
                char c = schema.convert(color);
                graph[h][w] = c;

            }
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[i].length; j++) {
                builder.append(graph[i][j]);
                builder.append(graph[i][j]);
            }
            builder.append("\n");

        }
        String str = builder.toString();
        return str;
    }

    @Override
    public void setMaxWidth(int width) {
        this.width = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.height = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema colorSchema) {
        this.schema = colorSchema;
    }
}