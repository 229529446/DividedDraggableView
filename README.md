# DividedDraggableView

Draggable grid view with divided line.

<img src="https://github.com/andyken/DividedDraggableView/blob/master/app/sample.gif"/>

## Usage
Add the dependency to your build.gradle.

    compile 'com.github.andyken:divideddraggableview:1.2'

## Usage

You should new the DividedDraggableView and setItemCount,then add your view by calling addChildView.

    DividedDraggableView dividedDraggableView = new DividedDraggableView(SampleActivity.this);
    dividedDraggableView.setItemCount(mockViews.size());
    for (ImageView imageView : mockViews) {
        dividedDraggableView.addChildView(imageView);
    }
    rootView.addView(dividedDraggableView);

- you can set the attributes at xml or by using DividedDraggableView.Builder.
- you can set the attributes such as rowHeight,itemWidth,itemHeight,rowPadding.
- you can set the gap between group,group line count,group item count.text in group.
- you can set some color attributes like background color,gap color,group color,the color of text in gap.