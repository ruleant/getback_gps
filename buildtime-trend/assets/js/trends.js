/* vim: set expandtab sw=4 ts=4: */
/**
 * Analyse and visualise trend data using the Keen.io API.
 *
 * Copyright (C) 2014 Dieter Adriaenssens <ruleant@users.sourceforge.net>
 *
 * This file is part of buildtime-trend
 * <https://github.com/ruleant/buildtime-trend/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

var TIMEFRAME_LAST_WEEK = "this_7_days";
var TIMEFRAME_LAST_MONTH = "this_30_days";
var TIMEFRAME_LAST_YEAR = "this_52_weeks";

var CAPTION_LAST_WEEK = "Last week";
var CAPTION_LAST_MONTH = "Last month";
var CAPTION_LAST_YEAR = "Last year";

function getUpdatePeriod(period) {
    var keenTimeframe, keenInterval;

    switch (period) {
    case "day":
        keenTimeframe = "today";
        keenInterval = "hourly";
        break;
    default:
        period = "week";
    case "week":
        keenTimeframe = TIMEFRAME_LAST_WEEK;
        keenInterval = "daily";
        break;
    case "month":
        keenTimeframe = TIMEFRAME_LAST_MONTH;
        keenInterval = "daily";
        break;
    case "year":
        keenTimeframe = TIMEFRAME_LAST_YEAR;
        keenInterval = "weekly";
        break;
    }

    return {
        name: period,
        keenTimeframe: keenTimeframe,
        keenInterval: keenInterval
    };
}

// arrays with queries and query request to update
var queriesInterval = [];
var queriesTimeframe = [];
var queryRequests = [];

function updateCharts(periodName) {
    // get Update Period settings
    var updatePeriod = getUpdatePeriod(periodName);

    var i;

    // update all interval based queries
    for (i = 0; i < queriesInterval.length; i++) {
        queriesInterval[i].set({interval: updatePeriod.keenInterval});
    }

    // update all timeframe based queries
    for (i = 0; i < queriesTimeframe.length; i++) {
        queriesTimeframe[i].set({timeframe: updatePeriod.keenTimeframe});
    }

    // refresh all updated query requests
    for (i = 0; i < queryRequests.length; i++) {
        queryRequests[i].refresh();
    }
}

function initCharts() {
    // get Update Period settings
    var updatePeriod = getUpdatePeriod();

    var keenTimeframe = updatePeriod.keenTimeframe;
    var keenInterval = updatePeriod.keenInterval;

    // update interval selection box
    $('#intervals').val(updatePeriod.name);

    // hide title
    document.getElementById("timeframe_title").style.display = "none";

    // visualization code goes here
    Keen.ready(function() {
        /* Total builds */
        // create query
        var queryTotalBuilds = new Keen.Query("count", {
            eventCollection: "builds",
            timeframe: keenTimeframe,
        });
        queriesTimeframe.push(queryTotalBuilds);

        // draw chart
        var requestTotalBuilds = client.run(queryTotalBuilds, function() {
            this.draw(document.getElementById("metric_total_builds"), {
                title: "Total build jobs", width: "200"
            });
        });
        queryRequests.push(requestTotalBuilds);

        /* Total builds passed */
        // create query
        var queryTotalBuildsPassed = new Keen.Query("count", {
            eventCollection: "builds",
            timeframe: keenTimeframe,
            filters: [{"property_name":"build.result","operator":"eq","property_value":"passed"}]
        });
        queriesTimeframe.push(queryTotalBuildsPassed);

        // combine queries for conditional coloring of TotalBuildspassed
        var colorBuildsPassed = client.run([queryTotalBuilds, queryTotalBuildsPassed], function(result){
            var chartColor = ["green"];
            var totalBuilds = result[0].result;
            var totalBuildsPassed = result[1].result;

            if (totalBuilds === totalBuildsPassed) {
                chartColor = ["green"];
            } else if (totalBuilds > 0) {
                if ((totalBuildsPassed / totalBuilds) >= 0.75) {
                    chartColor = ["orange"];
                } else {
                    chartColor = ["red"];
                }
            }

            // draw chart
            client.draw(queryTotalBuildsPassed, document.getElementById("metric_total_builds_passed"),
                {
                    title: "Build jobs passed",
                    colors: chartColor,
                    width: "200"
                }
            );
        });
        queryRequests.push(colorBuildsPassed);

        /* Total builds passed */
        // create query
        var queryTotalBuildsFailed = new Keen.Query("count", {
            eventCollection: "builds",
            timeframe: keenTimeframe,
            filters: [{"property_name":"build.result","operator":"in","property_value":["failed","errored"]}]
        });
        queriesTimeframe.push(queryTotalBuildsFailed);

        // combine queries for conditional coloring of TotalBuildsfailed
        var colorBuildsFailed = client.run([queryTotalBuilds, queryTotalBuildsFailed], function(result){
            var chartColor = ["green"];
            var totalBuilds = result[0].result;
            var totalBuildsFailed = result[1].result;

            if (totalBuildsFailed === 0) {
                chartColor = ["green"];
            } else if (totalBuilds > 0) {
                if ((totalBuildsFailed / totalBuilds) <= 0.25) {
                    chartColor = ["orange"];
                } else {
                    chartColor = ["red"];
                }
            }

            // draw chart
            client.draw(queryTotalBuildsFailed, document.getElementById("metric_total_builds_failed"),
                {
                    title: "Build jobs failed",
                    colors: chartColor,
                    width: "200"
                }
            );
        });
        queryRequests.push(colorBuildsFailed);

        /* average build time of all stages */
        // create query
        var queryAverageBuildTime = new Keen.Query("average", {
            eventCollection: "builds",
            timeframe: keenTimeframe,
            targetProperty: "build.duration"
        });
        queriesTimeframe.push(queryAverageBuildTime);

        // draw chart
        var requestAverageBuildTime = client.run(queryAverageBuildTime, function() {
            this.draw(document.getElementById("metric_average_build_time"), {
                title: "Average job duration",
                width: "250",
                chartOptions: {
                    suffix: "s"
                }
            });
        });
        queryRequests.push(requestAverageBuildTime);

        /* average stage duration */
        // create query
        var queryStageDuration = new Keen.Query("average", {
            eventCollection: "build_stages",
            timeframe: keenTimeframe,
            interval: keenInterval,
            targetProperty: "stage.duration",
            groupBy: "stage.name",
            filters: [{"property_name":"stage.name","operator":"exists","property_value":true}]
        });
        queriesTimeframe.push(queryStageDuration);
        queriesInterval.push(queryStageDuration);

        // draw chart
        var requestStageDuration = client.run(queryStageDuration, function() {
            this.draw(document.getElementById("chart_stage_duration"), {
                chartType: "columnchart",
                title: "Average build stage duration",
                chartOptions: {
                    isStacked: true,
                    vAxis: {title: "duration [s]"}
                }
            });
        });
        queryRequests.push(requestStageDuration);

        /* Stage duration fraction */
        // create query
        var queryStageFraction = new Keen.Query("average", {
            eventCollection: "build_stages",
            timeframe: keenTimeframe,
            targetProperty: "stage.duration",
            groupBy: "stage.name",
            filters: [{"property_name":"stage.name","operator":"exists","property_value":true}]
        });
        queriesTimeframe.push(queryStageFraction);

        // draw chart
        var requestStageFraction = client.run(queryStageFraction, function() {
            this.draw(document.getElementById("chart_stage_fraction"), {
                title: "Build stage fraction of total build duration"
            });
        });
        queryRequests.push(requestStageFraction);

        /* Builds */
        // create query
        var queryBuilds = new Keen.Query("count_unique", {
            eventCollection: "build_stages",
            timeframe: keenTimeframe,
            interval: keenInterval,
            targetProperty: "build.build",
            groupBy: "build.branch"
        });
        queriesTimeframe.push(queryBuilds);
        queriesInterval.push(queryBuilds);

        // draw chart
        var requestBuilds = client.run(queryBuilds, function() {
            this.draw(document.getElementById("chart_builds"), {
                chartType: "columnchart",
                title: "Builds per branch",
                chartOptions: {
                    isStacked: true,
                    vAxis: {title: "build count"}
                }
            });
        });
        queryRequests.push(requestBuilds);

        /* Builds per branch */
        // create query
        var queryTotalBuildsBranch = new Keen.Query("count_unique", {
            eventCollection: "build_stages",
            timeframe: keenTimeframe,
            targetProperty: "build.build",
            groupBy: "build.branch"
        });
        queriesTimeframe.push(queryTotalBuildsBranch);

        // draw chart
        var requestTotalBuildsBranch = client.run(queryTotalBuildsBranch, function() {
            this.draw(document.getElementById("chart_total_builds_branch"), {
                title: "Builds per branch (%)"
            });
        });
        queryRequests.push(requestTotalBuildsBranch);

        /* Average buildtime per time of day */
        // create query
        var queryAvgBuildtimeHourLastWeek = new Keen.Query("average", {
            eventCollection: "builds",
            timeframe: TIMEFRAME_LAST_WEEK,
            targetProperty: "build.duration",
            groupBy: "build.started_at.hour_24",
            filters: [{"property_name":"build.started_at.hour_24","operator":"exists","property_value":true}]
        });
        var queryAvgBuildtimeHourLastMonth = new Keen.Query("average", {
            eventCollection: "builds",
            timeframe: TIMEFRAME_LAST_MONTH,
            targetProperty: "build.duration",
            groupBy: "build.started_at.hour_24",
            filters: [{"property_name":"build.started_at.hour_24","operator":"exists","property_value":true}]
        });
        var queryAvgBuildtimeHourLastYear = new Keen.Query("average", {
            eventCollection: "builds",
            timeframe: TIMEFRAME_LAST_YEAR,
            targetProperty: "build.duration",
            groupBy: "build.started_at.hour_24",
            filters: [{"property_name":"build.started_at.hour_24","operator":"exists","property_value":true}]
        });

        // generate chart
        var requestAvgBuildtimeHour = client.run(
                [queryAvgBuildtimeHourLastWeek,
                    queryAvgBuildtimeHourLastMonth,
                    queryAvgBuildtimeHourLastYear],
                function()
        {
            timeframe_captions = [CAPTION_LAST_WEEK, CAPTION_LAST_MONTH, CAPTION_LAST_YEAR];
            index_captions = [];
            // populate array with an entry per hour
            for (i = 0; i < 24; i++) {
                index_captions[i]= String(i) + ":00";
            }

            chart_data = mergeSeries(
                this.data,
                index_captions,
                "build.started_at.hour_24",
                timeframe_captions
            );

            // draw chart
            window.chart = new Keen.Visualization(
                {result: chart_data},
                document.getElementById("chart_avg_buildtime_hour"),
                {
                    chartType: "columnchart",
                    title: "Average buildtime per time of day",
                    chartOptions: {
                    vAxis: { title: "duration [s]" },
                    hAxis: {
                        title: "Time of day [24-hour format, UTC]",
                        slantedText: "true",
                        slantedTextAngle: "90"
                    }
                }
            });
        });
        queryRequests.push(requestAvgBuildtimeHour);

        /* Average buildtime per day of week */
        // create query
        var queryAvgBuildtimeWeekDayLastWeek = new Keen.Query("average", {
            eventCollection: "builds",
            timeframe: TIMEFRAME_LAST_WEEK,
            targetProperty: "build.duration",
            groupBy: "build.started_at.day_of_week",
            filters: [
                {
                    "property_name":"build.started_at.day_of_week",
                    "operator":"exists",
                    "property_value":true
                }
            ]
        });
        var queryAvgBuildtimeWeekDayLastMonth = new Keen.Query("average", {
            eventCollection: "builds",
            timeframe: TIMEFRAME_LAST_MONTH,
            targetProperty: "build.duration",
            groupBy: "build.started_at.day_of_week",
            filters: [
                {
                    "property_name":"build.started_at.day_of_week",
                    "operator":"exists",
                    "property_value":true
                }
            ]
        });
        var queryAvgBuildtimeWeekDayLastYear = new Keen.Query("average", {
            eventCollection: "builds",
            timeframe: TIMEFRAME_LAST_YEAR,
            targetProperty: "build.duration",
            groupBy: "build.started_at.day_of_week",
            filters: [
                {
                    "property_name":"build.started_at.day_of_week",
                    "operator":"exists",
                    "property_value":true
                }
            ]
        });

        // generate chart
        var requestAvgBuildtimeWeekDay = client.run(
                [queryAvgBuildtimeWeekDayLastWeek,
                    queryAvgBuildtimeWeekDayLastMonth,
                    queryAvgBuildtimeWeekDayLastYear],
                function()
        {
            timeframe_captions = [CAPTION_LAST_WEEK, CAPTION_LAST_MONTH, CAPTION_LAST_YEAR];
            index_captions = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
            chart_data = mergeSeries(
                this.data,
                index_captions,
                "build.started_at.day_of_week",
                timeframe_captions
            );

            // draw chart
            window.chart = new Keen.Visualization(
                {result: chart_data},
                document.getElementById("chart_avg_buildtime_weekday"),
                {
                    chartType: "columnchart",
                    title: "Average buildtime per day of week",
                    chartOptions: {
                        vAxis: { title: "duration [s]" },
                        hAxis: { title: "Day of week" }
                }
            });
        });
        queryRequests.push(requestAvgBuildtimeWeekDay);
    });
}

// add project name to title
function updateTitle() {
    // check if config.projectName is set
    if (config.projectName !== null || config.projectName == 'project_name') {
        var title = 'Build trends of project ' + htmlEntities(config.projectName);
        document.getElementById("title").innerHTML = title;
        document.getElementsByTagName("title")[0].innerHTML = title;
    }
}

// escape html characters
// inspired by http://css-tricks.com/snippets/javascript/htmlentities-for-javascript/
function htmlEntities(str) {
    return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#039;');
}

/**
 * Merge data from several series, with identical X-axis labels
 * 
 * Parameters :
 * - data : Keen.io query results
 * - index_captions : Captions for the index of the values
 * - value_fieldname : name of the value field in the query result array
 * - series_captions : captions for the data series
 */
function mergeSeries(data, index_captions, value_fieldname, series_captions) {
    chart_data = [];
    // create and populate data array
    for (i = 0; i < index_captions.length; i++) {
        chart_data[i]={caption: index_captions[i]};
        // populate all series
        for (j = 0; j < series_captions.length; j++) {
            chart_data[i][series_captions[j]] = 0;
        }
    }
    // loop over all query result sets
    for (j = 0; j < data.length; j++) {
        timeframe_result = data[j].result;
        timeframe_caption = series_captions[j];
        // copy query data into the populated array
        for (i = 0; i < timeframe_result.length; i++) {
            index = parseInt(timeframe_result[i][value_fieldname])
            chart_data[index][timeframe_caption] = timeframe_result[i]["result"];
        }
    }

    return chart_data;
}

// initialize page
$(document).ready(function() {
    updateTitle();
    initCharts();
});
