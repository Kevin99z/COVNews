package com.zhangzeyuan.cov_news.ui.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.charts.Cartesian;
import com.anychart.data.Set;
import com.anychart.enums.ScaleStackMode;
import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.zhangzeyuan.cov_news.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StatFragment extends Fragment {

    private StatViewModel statViewModel;
    private AnyChartView chartView;
    private SmartTable<ProvInfo> table;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statViewModel = ViewModelProviders.of(getActivity()).get(StatViewModel.class);
        View root = inflater.inflate(R.layout.fragment_stat, container, false);
        TextView tv = root.findViewById(R.id.title1);
        //世界疫情
        tv.setText("世界疫情："+SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(new Date(System.currentTimeMillis())));
//        final TextView textView = root.findViewById(R.id.text_dashboard);
//        final SmartTable table = root.findViewById(R.id.table);
        chartView = root.findViewById(R.id.chart);
        APIlib.getInstance().setActiveAnyChartView(chartView);
        Cartesian chart = AnyChart.column();
        chart.yScale().stackMode(ScaleStackMode.VALUE);
        chart.yAxis(0).labels().format("{%Value}{scale:(10000)(1)|(万)}");
        chart.title("全球确诊人数");
//        chart.yScale().ticks().interval(200000);
        Set set = Set.instantiate();
        chart.column(set.mapAs("{ x: 'x', value: 'recovered' }")).tooltip().format("痊愈: {%value}");
        chart.column(set.mapAs("{ x: 'x', value: 'deaths' }")).tooltip().format("死亡: {%value}");
        chart.column(set.mapAs("{ x: 'x', value: 'active' }")).tooltip().format("现存: {%value}");
        chart.legend();
        chartView.setChart(chart);
        // 国内疫情
        table = root.findViewById(R.id.table);
        TableConfig config = table.getConfig();
        config.setShowTableTitle(false);
        config.setShowXSequence(false);
        config.setShowYSequence(false);
        statViewModel.getProvInfo().observe(getViewLifecycleOwner(), data->{
            table.setData(data);
        });
        statViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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
        statViewModel.getSeriesData().observe(getViewLifecycleOwner(), seriesData -> {
//                        chart.animation(true);
            APIlib.getInstance().setActiveAnyChartView(chartView);
            set.data(seriesData);
        });

        statViewModel.fetchGlobalData();
        statViewModel.fetchChinaData();
        return root;
    }
}