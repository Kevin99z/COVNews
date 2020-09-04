package com.example.cov_news.ui.dashboard;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.data.Set;
import com.anychart.enums.ScaleStackMode;
import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.draw.IDrawFormat;
import com.bin.david.form.data.table.MapTableData;
import com.bin.david.form.core.TableConfig;
import com.example.cov_news.R;
import com.example.cov_news.JsonHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private AnyChartView chartView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(getActivity()).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        TextView tv = root.findViewById(R.id.title1);
        tv.setText("世界疫情："+SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(new Date(System.currentTimeMillis())));
//        final TextView textView = root.findViewById(R.id.text_dashboard);
//        final SmartTable table = root.findViewById(R.id.table);
        chartView = root.findViewById(R.id.chart);
        APIlib.getInstance().setActiveAnyChartView(chartView);
        Cartesian chart = AnyChart.column();
        chart.yScale().stackMode(ScaleStackMode.VALUE);
//        chart.yScale().ticks().interval(200000);
        Set set = Set.instantiate();
        chart.column(set.mapAs("{ x: 'x', value: 'recovered' }")).tooltip().format("痊愈: {%value}");
        chart.column(set.mapAs("{ x: 'x', value: 'death' }")).tooltip().format("死亡: {%value}");
        chart.column(set.mapAs("{ x: 'x', value: 'active' }")).tooltip().format("现存: {%value}");
        chartView.setChart(chart);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if(!s.equals("")) {
//                    MapTableData data = MapTableData.create("全球疫情", list);
//                    TableConfig config = table.getConfig();
//                    Column col = data.getColumns().get(0);
//                    col.setTextAlign(Paint.Align.LEFT);
//                    col.setFixed(true);
//                    config.setShowYSequence(false);
//                    config.setShowXSequence(false);
//                    table.setTableData(data);
//                textView.setText(s);
                }
            }
        });
        dashboardViewModel.getSeriesData().observe(getViewLifecycleOwner(), seriesData -> {
//                        chart.animation(true);
            APIlib.getInstance().setActiveAnyChartView(chartView);
            set.data(seriesData);
        });

        dashboardViewModel.fetchData();

        return root;
    }
}