function updateCharts(period) {
  var keenTimeframe, keenInterval;

  switch (period) {
    case "day":
      keenTimeframe = "today";
      keenInterval = "hourly";
      break;
    default:
      period = "week";
    case "week":
      keenTimeframe = "this_7_days";
      keenInterval = "daily";
      break;
    case "month":
      keenTimeframe = "this_30_days";
      keenInterval = "daily";
      break;
    case "year":
      keenTimeframe = "this_52_weeks";
      keenInterval = "weekly";
      break;
  }
  
  // update interval selection box
  $('#intervals').val(period);

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

    // draw chart
    client.draw(queryTotalBuilds, document.getElementById("metric_total_builds"),
      { title: "Total build jobs", width: "200px" });

    // display div inline (show it next to the next chart)
    document.getElementById("metric_total_builds").style.display = "inline-block";

    /* Total builds passed */
    // create query
    var queryTotalBuildsPassed = new Keen.Query("count", {
      eventCollection: "builds",
      timeframe: keenTimeframe,
      filters: [{"property_name":"build.result","operator":"eq","property_value":"passed"}]
    });

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
          width: "200px"
        }
      );
    });

    // display div inline (show it next to the next chart)
    document.getElementById("metric_total_builds_passed").style.display = "inline-block";

    /* Total builds passed */
    // create query
    var queryTotalBuildsFailed = new Keen.Query("count", {
      eventCollection: "builds",
      timeframe: keenTimeframe,
      filters: [{"property_name":"build.result","operator":"in","property_value":["failed","errored"]}]
    });

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
          width: "200px"
        }
      );
    });

    // display div inline (show it next to the previous chart)
    document.getElementById("metric_total_builds_failed").style.display = "inline-block";

    /* average build time of all stages */
    // create query
    var queryAverageBuildTime = new Keen.Query("average", {
      eventCollection: "builds",
      timeframe: keenTimeframe,
      targetProperty: "build.duration"
    });

    // draw chart
    client.draw(queryAverageBuildTime, document.getElementById("metric_average_build_time"),
      {
        title: "Average job duration",
        width: "250px",
        chartOptions: {
          suffix: "s"
        }
      }
    );

    // display div inline (show it next to the previous chart)
    document.getElementById("metric_average_build_time").style.display = "inline-block";

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

    // draw chart
    client.draw(queryStageDuration, document.getElementById("chart_stage_duration"),
      {
        chartType: "columnchart",
        title: "Average build stage duration",
        chartOptions: {
          isStacked: true,
          chartArea: {left: 75, width: 350},
          vAxis: {title: "duration [s]"}
        }
      }
    );

    // display div inline (show it next to the next chart)
    document.getElementById("chart_stage_duration").style.display = "inline-block";

    /* Stage duration fraction */
    // create query
    var queryStageFraction = new Keen.Query("average", {
      eventCollection: "build_stages",
      timeframe: keenTimeframe,
      targetProperty: "stage.duration",
      groupBy: "stage.name",
      filters: [{"property_name":"stage.name","operator":"exists","property_value":true}]
    });

    // draw chart
    client.draw(queryStageFraction, document.getElementById("chart_stage_fraction"),
      { title: "Build stage fraction of total build duration" });

    // display div inline (show it next to the previous chart)
    document.getElementById("chart_stage_fraction").style.display = "inline-block";

    /* Builds */
    // create query
    var queryBuilds = new Keen.Query("count_unique", {
      eventCollection: "build_stages",
      timeframe: keenTimeframe,
      interval: keenInterval,
      targetProperty: "build.build",
      groupBy: "build.branch"
    });

    // draw chart
    client.draw(queryBuilds, document.getElementById("chart_builds"),
      {
        chartType: "columnchart",
        title: "Builds per branch",
        chartOptions: {
          isStacked: true,
          chartArea: {left: 75, width: 350},
          vAxis: {title: "build count"}
        }
      }
    );

    // display div inline (show it next to the next chart)
    document.getElementById("chart_builds").style.display = "inline-block";

    /* Builds per branch */
    // create query
    var queryTotalBuildsBranch = new Keen.Query("count_unique", {
      eventCollection: "build_stages",
      timeframe: keenTimeframe,
      targetProperty: "build.build",
      groupBy: "build.branch"
    });

    // draw chart
    client.draw(queryTotalBuildsBranch, document.getElementById("chart_total_builds_branch"),
      { title: "Builds per branch (%)" });

    // display div inline (show it next to the previous chart)
    document.getElementById("chart_total_builds_branch").style.display = "inline-block";
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

// initialize page
function initPage() {
  updateTitle();
  updateCharts();
}
