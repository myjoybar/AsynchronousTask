
# Android-Router
Asynchronous Task

 ![image](https://github.com/myjoybar/Android-Router/blob/master/screenshots/screenshot.jpg)

## Features
 - 一个类似于asyncTask的异步任务执行器

 
## Installation
### Gradle Dependency
#####   Add the library to your project build.gradle
```gradle
compile 'com.joybar.asynchronoustask:library:1.0.0'
```




## Sample Usage

1 初始化AsynchronousTask

```java
AsynchronousTask asynchronousTask = new AsynchronousTask<Integer, String>() {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "主线程：执行前");
    }

    @Override
    protected String doInBackground() {
        Log.d(TAG, "子线程：" + getThreadName() + "后台执行");
        try {
            Thread.sleep(1 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.publishProgress(1);
        return "A"+1;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.d(TAG, getThreadName() + "执行进度=" + values[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d(TAG, getThreadName() + "执行结果=" + result);

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d(TAG, getThreadName() + "任务取消" );
    }
};

```
2 执行asynchronousTask

```java
AsyncFactory.getInstance().produce(asynchronousTask);

```
3 取消执行执行asynchronousTask

```java
asynchronousTask.cancel(false);

```
## License

    Copyright 2017 MyJoybar

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.    
        