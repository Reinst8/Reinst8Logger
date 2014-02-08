<?php require_once("logViewer.php"); ?>
<!DOCTYPE html>
<html class="no-js">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title></title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">
    <link rel="stylesheet" href="css/main.css">
    <link rel="stylesheet" href="css/normalize.min.css">
    <link href='http://fonts.googleapis.com/css?family=Roboto:400' rel='stylesheet' type='text/css'>
    <link href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.0.3/css/font-awesome.min.css" type="text/css"
          rel="stylesheet">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/ink/2.2.1/css/ink-min.css">
    <script src="js/vendor/modernizr-2.6.2.min.js"></script>
</head>
<body>
<div class="header grid-100">

    <nav class="ink-navigation">
        <ul class="menu blue horizontal">
            <li class="nav-header">Reinst8 Log Viewer</li>
            <li><a href="http://reinst8.org.uk/">Official Site</a></li>
            <li><a href="http://www.reddit.com/r/ReinstateArticle8">Reddit</a></li>
            <li><a href="http://www.twitter.com/Reinst8">Twitter</a></li>
        </ul>
    </nav>
</div>

<main>

    <div class="grid-25">
        <nav class="ink-navigation">
            <ul class="menu vertical blue rounded shadowed">
                <li class="nav-header">Select log file</li>
                <?php foreach ($dateList as $date => $active): ?>
                    <li <?php echo ($active) ? 'class="active"' : '' ?>>
                        <a href="/?file=<?php echo($date); ?>"><?php echo date("l, jS F Y", $date) ?></a>
                    </li>
                <?php endforeach; ?>
            </ul>
        </nav>
    </div>
    <div class="grid-50">
        <?php if (isset($selectedFile) && !validFile($selectedFile)): ?>
            <h3>Invalid file</h3>
            <p>Please select a valid file from the list on the left.</p>
        <?php else: ?>
            <?php echo (empty($selectedFile)) ? file_get_contents("intro.htm") : '<h3>Viewing log from ' . date("l, jS F Y", $selectedFile) . '</h3><div class="chatLog">' . file_get_contents($logDirectory . $fileList[$selectedFile]) . "</div>"; ?>
        <?php endif; ?>
    </div>
</main>

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>
<script>window.jQuery || document.write('<script src="js/vendor/jquery-1.10.1.min.js"><\/script>')</script>

<script src="js/main.js"></script>
</body>
</html>
