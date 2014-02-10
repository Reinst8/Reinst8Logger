<?php
/* I'm not going to make this class-based for a small project like this */
error_reporting(E_ALL);
/* ============= Begin Config ============= */
$logDirectory = "C:\\Logs\\";
/* ============= End Config ============= */

// Get all files matching *.htm in the specified dir
$directoryIterator = new DirectoryIterator($logDirectory);
$dateList = [];
$fileList = [];
$selectedFile = null;

if (isset($_GET['file'])) {
    $selectedFile = $_GET['file'];
}

foreach ($directoryIterator as $fileInfo) {
    if ($fileInfo->isFile() && $fileInfo->getExtension() == "html") {
        $fn = $fileInfo->getFilename();
        $fn = explode("-", $fn);
        if (count($fn) != 2) {
            die("Invalid file format.");
        }
        $fn = substr($fn[1], 0, -5);
        $fn = (int) ($fn / 1000);
        $dateList[$fn] = ($selectedFile == $fn);
        $fileList[$fn] = $fileInfo->getFilename();
    }
}

function validFile($fn) {
    global $fileList;
    return (array_key_exists($fn, $fileList));
}

function nextMeeting() {
    date_default_timezone_set('UTC');
	$dayOfWeek = date('N');
	$hr = date('G');
	if ($dayOfWeek == 1 && $hr < 20) {
		//Monday
		return strtotime('today 20:00');
	} else if ($dayOfWeek == 4 && $hr < 20) {
		//Thursday
		return strtotime('today 20:00');
	} else {
		$nextMonday = strtotime('Next Monday 20:00');
		$nextThursday = strtotime('Next Thursday 20:00');
		return ($nextMonday < $nextThursday) ? $nextMonday : $nextThursday;
	}
}
