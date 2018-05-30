from time import sleep
import subprocess

import gpxpy.gpx


""" Parsing an existing file: """
gpx_file = open('testRoute.gpx', 'r')
gpx = gpxpy.parse(gpx_file)
proc = subprocess.Popen(
    ["/Applications/Genymotion Shell.app/Contents/MacOS/genyshell"],
    stdin=subprocess.PIPE,
    stderr=subprocess.STDOUT
)
sleep(2)
proc.stdin.write(bytes("gps setstatus enabled\n", "utf-8"))
proc.stdin.flush()
""" Convert 100km/hour in meter/second"""
speed = (20 * 1000) / 3600
previous_point = None
try:
    for track in gpx.tracks:
        for segment in track.segments:
            for i, point in enumerate(segment.points):
                if previous_point is not None:
                    distance = point.distance_2d(previous_point)
                    duration = distance / speed
                    sleep(duration)
                command = "gps setlatitude {}\ngps setlongitude {}\n".format(
                    point.latitude, point.longitude)
                proc.stdin.write(bytes(command, "utf-8"))
                proc.stdin.flush()
                previous_point = point
finally:
    proc.stdin.write(bytes("exit\n", "utf-8"))
    proc.stdin.flush()
    proc.terminate()
    proc.wait()
