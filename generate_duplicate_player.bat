@echo off

for /l %%x in (2, 1, 5) do (
	echo rule fr.unice.polytech.si3.qgl.soyouz.** fr.unice.polytech.si3.qgl.soyouz%%x.@1> rules.txt
	java -jar jarjar.jar --rules rules.txt player/target/soyouz-0.1-SNAPSHOT.jar --output player/target/soyouz%%x-0.1-SNAPSHOT.jar
)