# batch-demo-app
Processing large Amounts of Data and Controlling
1. Application.properties: csv.file.path
2. Application.properties: source.directory
3. Application.properties: destination.directory
4. Start Jobs: http://localhost:8080/jobs/start
5. Stop Jobs: http://localhost:8080/jobs/stop
6. Status Jobs: http://localhost:8080/jobs/status
7. If the Running Process is Stopped Manually, then it is required to start Manually
8. If a running process is stopped, then also it will move the current file executing
9. Disable Scheduler: http://localhost:8080/scheduler/disable
10. Enable Scheduler: http://localhost:8080/scheduler/enable
11. If the scheduler is disabled,  Start Jobs will execute only on the job
12. Import File: http://localhost:8080/jobs/importData
13. todo
14. 
