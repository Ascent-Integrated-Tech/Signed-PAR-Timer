//package com.aitech.atak.AnyaPlugin.components;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Typeface;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.View;
//
//import com.atakmap.android.routes.elevation.model.UnitConverter;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class CustomGraphView extends View {
//
//    private static final String TAG = "AnyaPluginCustomGraphView";
//    public enum DisplayUnit {
//        FEET("ft"),
//        METRES("m");
//
//        private final String displayValue;
//
//        DisplayUnit(String displayValue) {
//            this.displayValue = displayValue;
//        }
//
//        public String getDisplayValue() {
//            return displayValue;
//        }
//
//        public static DisplayUnit fromDisplayValue(String displayValue) {
//            for (DisplayUnit unit : DisplayUnit.values()) {
//                if (unit.getDisplayValue().equals(displayValue)) {
//                    return unit;
//                }
//            }
//            return DisplayUnit.METRES;
//        }
//    }
//
//    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//    private Map<String, DataPoint> dataPoints = new HashMap<>();
//    private double minValue = 0;
//    private double maxValue = 0;
//
//    private DisplayUnit displayUnit;
//
//    public CustomGraphView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        this.displayUnit = DisplayUnit.METRES;
//        Log.d(TAG, "CTR");
//    }
//
//    public void setUnit(DisplayUnit u) {
//        this.displayUnit  = u;
//    }
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        float width = getWidth();
//        float height = getHeight();
//        float padding = 20; // Padding around the graph
//        float labelBottomMargin = 50; // Margin for labels from the bottom
//        float graphHeight = height - 2 * padding - labelBottomMargin; // Adjust graph height for bottom label margin
//
//
//        // Ensure zero is always included in the graph range
//        minValue = Math.min(minValue, 0);
//        maxValue = Math.max(maxValue, 0);
//
//        // Avoid division by zero and ensure there is a range to draw
//        if (minValue == maxValue) {
//            maxValue += 1; // Ensure there is at least a unit range
//        }
//
//        double scale = graphHeight / (maxValue - minValue);
//        double interval = calculateInterval(minValue, maxValue, 10);
//
//        paint.setColor(Color.GRAY);
//        paint.setTextSize(30);
//        paint.setTextAlign(Paint.Align.RIGHT);
//
//        // Define padding for the labels on the left
//        float labelLeftPadding = 60; // Set this to accommodate your label width
//
//        // Draw horizontal lines and value labels
//        boolean zeroLineDrawn = false;
//
//        double lineValue = Math.ceil(minValue / interval) * interval;
//        while (lineValue <= maxValue) {
//            double y = padding + graphHeight - (lineValue - minValue) * scale;
//            if (lineValue == 0) {
//                paint.setStrokeWidth(4); // Thicker line for zero level
//                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD)); // Bold the zero line
//                paint.setColor(Color.RED); // Make zero line red for visibility
//                zeroLineDrawn = true;
//            } else {
//                paint.setStrokeWidth(2); // Standard thickness for other lines
//                paint.setTypeface(Typeface.DEFAULT); // Standard typeface for other lines
//                paint.setColor(Color.GRAY); // Standard color for other lines
//            }
//            canvas.drawLine(padding + labelLeftPadding, (float) y, width - padding, (float) y, paint);
//            canvas.drawText(String.format("%.0f", lineValue), padding + labelLeftPadding - 10, (float) (y + paint.getTextSize() / 3), paint);
//            lineValue += interval;
//        }
//
//        // Explicitly draw the zero line if not already drawn
//        if (!zeroLineDrawn) {
//            double yZero = padding + graphHeight - (-minValue * scale);
//            paint.setStrokeWidth(4); // Thicker line for zero level
//            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD)); // Bold the zero line
//            paint.setColor(Color.RED); // Make zero line red for visibility
//            canvas.drawLine(padding + labelLeftPadding, (float) yZero, width - padding, (float) yZero, paint);
//            canvas.drawText("0", padding + labelLeftPadding - 10, (float) (yZero + paint.getTextSize() / 3), paint);
//        }
//
//// Draw data points and vertical labels
//        float step = width / (dataPoints.size() + 1);
//        float currentX = step;
//        paint.setTextAlign(Paint.Align.CENTER); // Center align text
//        for (DataPoint point : dataPoints.values()) {
//            paint.setColor(point.color);
//            double dataY = padding + graphHeight - (point.altitude - minValue) * scale;
//            canvas.drawCircle(currentX, (float) dataY, 10, paint);
//
//            // Adjusted text drawing for vertical labels
//            canvas.save();
//            // Translate to position the text bottom aligned to the margin
//            canvas.translate(currentX, height - labelBottomMargin); // Adjust this translation to position labels above the bottom margin
//            canvas.rotate(-90);
//            paint.setTextAlign(Paint.Align.LEFT);
//            // Draw text such that the text is upwards but starts just at the translated point
//            canvas.drawText(point.displayName, -30, 0, paint); // Use positive paint.getTextSize() to move text up
//            canvas.restore();
//
//            currentX += step;
//        }
//
//        paint.setColor(Color.RED);
//        paint.setTextSize(40);
//        paint.setTextAlign(Paint.Align.LEFT);
//        canvas.drawText("[" + displayUnit.getDisplayValue() + "]", padding, height - padding, paint);
//    }
//
//    private double calculateInterval(double min, double max, int maxLines) {
//        double range = max - min;
//        double basicInterval = range / (maxLines - 1);
//        return Math.max(basicInterval, 1); // Ensure at least 1 or more
//    }
//
//    public void update(String uid, String displayName, Double altitude) {
//        if (displayUnit.equals(DisplayUnit.FEET))
//            altitude = UnitConverter.Meter.toFeet(altitude);
//        DataPoint point = new DataPoint(displayName, altitude, getColorForAltitude(altitude));
//        dataPoints.put(uid, point);
//        recalculateScale();
//        invalidate(); // Redraw the view
//    }
//
//    private void recalculateScale() {
//        double currentMin = Double.MAX_VALUE;
//        double currentMax = Double.MIN_VALUE;
//        for (DataPoint point : dataPoints.values()) {
//            if (point.altitude < currentMin) currentMin = point.altitude;
//            if (point.altitude > currentMax) currentMax = point.altitude;
//        }
//
//        // Ensure zero is included in the scale
//        if (currentMin > 0) currentMin = 0;
//        if (currentMax < 0) currentMax = 0;
//        // Adjust to definitely include zero in the range
//        currentMin = Math.min(currentMin, 0);
//        currentMax = Math.max(currentMax, 0);
//
//        // Maintain the overall min and max values
//        minValue = Math.min(minValue, currentMin);
//        maxValue = Math.max(maxValue, currentMax);
//    }
//
//
//    private int getColorForAltitude(Double altitude) {
//        return Color.GREEN; // Simplified for brevity
//    }
//
//    private static class DataPoint {
//        String displayName;
//        Double altitude;
//        int color;
//
//        DataPoint(String displayName, Double altitude, int color) {
//            this.displayName = displayName;
//            this.altitude = altitude;
//            this.color = color;
//        }
//    }
//}
