![Image](/pic/shyshark_banner.png)
# ShyShark 🦈
ShyShark is Swipeable card stack view like Tinder.


## What's New in newest version? :tada:
- Fix card swipe animation to match swiping direction([#4](https://github.com/sabujak-sabujak/ShyShark/issues/4))

## Result Screen

| Project Name | Result Screen   |
|:---------:|---|
| Sample  |  <img src="/pic/sample.gif"> |

## How to Use

### Gradle

```groovy
    dependencies {
        implementation 'io.github.sabujak-sabujak:shyshark:x.x.x'
    }
```
latest version [![Maven Central](https://img.shields.io/maven-central/v/io.github.sabujak-sabujak/shyshark)](https://search.maven.org/artifact/io.github.sabujak-sabujak/shyshark)
### Usage
```xml
            <life.sabujak.shyshark.ShySharkView
                android:id="@+id/recyclerView"
                android:layout_width="match_parent"
                android:layout_height="0dp"
                android:layout_marginBottom="16dp"
                app:autoDraggingAnimationDuration="200"
                app:dragThrashold="0.1"
                app:layout_constraintBottom_toTopOf="@+id/fab_main_good"
                app:layout_constraintTop_toTopOf="parent"
                app:restoreScaleAnimationDuration="200"
                app:scaleGap="0.5"
                app:swipeableFlag="swipe_horizontal" />
```

```kotlin
        recyclerView.setOnSwipeListener(object :
            OnSwipeListener {
            override fun onSwiped(position: Int, direction: Int) {
            }

            override fun onChangeHorizontalDrag(direction: Int, percent: Float) {
            }

            override fun onChangeVerticalDrag(direction: Int, percent: Float) {
            }
        })
```

#### attribute

|      Attribute Name        | Description                               |       Default Value      |
|:--------------------------:|-------------------------------------------|:------------------------:|
|       swipeableFlag        | Swipeable direction                       | LEFT, RIGHT, TOP, BOTTOM |
|        preloadCount        | Preloaded item count                      |             3            |
|          scaleGap          | Scale gap per item                        |            0.1f          |
|        dragThrashold       | Trashold value passed by drag             |            0.2f          |
|       defaultElevation     | z-axis value                              |             0f           |
|     restoreScaleAnimationDuration    | Card restore animation duration |            200L          |
|     autoDraggingAnimationDuration    | Animation duration automatically dragged |   200L          |

*defaultElevation : If the view itself has an elevation value, it will have that value as default.
If the elevation value of the view itself is larger than the setting value, the setting value is ignored.*

# Contribute
Welcome any contributions.

# License

    Copyright 2020 Sabujak-Sabujak

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
