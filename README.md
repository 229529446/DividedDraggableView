# DividedDraggableView

Draggable grid view with divided line.

<img src="https://github.com/andyken/DividedDraggableView/blob/master/app/sample.gif"/>

## Usage
Add the dependency to your build.gradle.

    compile 'com.github.andyken:divideddraggableview:1.1'

## Usage

You should new the DividedDraggableView and setItemCount,then add your view by calling addChildView.

    DividedDraggableView dividedDraggableView = new DividedDraggableView(SampleActivity.this);
    dividedDraggableView.setItemCount(mockViews.size());
    for (ImageView imageView : mockViews) {
        dividedDraggableView.addChildView(imageView);
    }
    rootView.addView(dividedDraggableView);