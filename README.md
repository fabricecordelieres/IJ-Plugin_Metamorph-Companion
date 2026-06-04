# Metamorph Companion

Metamorph Companion is a Fiji/ImageJ plugin designed to facilitate the manipulation of Metamorph-specific files outside the acquisition software.

It provides tools to import multidimensional acquisitions, exchange regions of interest, generate stage position files and prepare complex automated acquisition workflows.

The plugin was developed to support routine microscopy experiments involving multi-position imaging, plate screening, FRAP experiments and automated acquisitions.

---

# What was the user's request?

Metamorph is widely used for microscope control and automated image acquisition. However, once an experiment has been acquired, users often need to:

- Open complex multidimensional datasets in Fiji;
- Reconstruct hyperstacks from Metamorph acquisitions;
- Transfer regions of interest between Metamorph and Fiji;
- Generate acquisition positions for multi-well plates;
- Adapt existing acquisition layouts to a new sample;
- Optimize stage trajectories for large screening experiments.

Performing these operations manually is time-consuming and error-prone.

Metamorph Companion was developed to automate these tasks and provide a direct bridge between acquisition and image analysis.

---

# Overview

The plugin is organized around three major Metamorph file formats:

```text
ND   → Acquisition descriptors
RGN  → Regions of interest
STG  → Stage positions
```

Each family of tools addresses a different stage of the microscopy workflow.

---

# Working with ND Files

ND files describe multidimensional acquisitions performed in Metamorph.

They contain information about:

- channels
- positions
- z-sections
- time points

but not the image data themselves.

## Typical workflow

```text
Metamorph Acquisition
         │
         ▼
       ND File
         │
         ▼
 Metamorph Companion
         │
         ▼
 Fiji Hyperstack
```

### Import ND Acquisition

The plugin reconstructs Fiji hyperstacks directly from an ND file.

Available options:

- channel selection
- position selection
- time point selection
- z projection
- batch processing
- automatic hyperstack generation

This removes the need to manually reorganize image files.

---

### Batch Import

When dozens or hundreds of acquisitions have been generated, entire directories can be processed automatically.

Typical applications:

- screening experiments
- long-term timelapses
- multi-position acquisitions

---

### Generate ND Files

The plugin can also generate Metamorph-compatible ND files.

This is useful when preparing acquisition templates involving:

- multiple channels
- multiple positions
- z-stacks
- timelapses

---

### Live Acquisition Plot

During long acquisitions, Metamorph Companion can monitor the progression of image generation and display acquisition status in real time.

---

# Working with RGN Files

RGN files store regions of interest used during acquisition.

These regions often need to be reused during image analysis.

## Import Regions

```text
Metamorph RGN
      │
      ▼
 ROI Manager
```

Supported region types include:

- rectangle
- polygon
- polyline
- oval
- freehand

---

## Export Regions

```text
ROI Manager
     │
     ▼
 Metamorph RGN
```

This feature allows regions generated from image analysis to be reused directly during acquisition.

Typical example:

```text
Image Analysis
      │
      ▼
 Segmentation
      │
      ▼
 ROI Export
      │
      ▼
 Targeted Acquisition
```

---

# Working with STG Files

STG files define stage coordinates used by automated acquisitions.

This module provides a complete toolbox to manipulate these coordinates.

---

## Visualize Stage Positions

Generate graphical maps showing:

- acquisition positions
- acquisition order
- position names
- stage trajectories

Example:

```text
Well A1 ──► Well A2 ──► Well A3
    ▲                      │
    └──────────────────────┘
```

Such maps are particularly useful before launching long automated acquisitions.

---

## Transform Stage Positions

Apply geometric transformations to an entire acquisition layout.

Supported operations:

- translation
- rotation
- scaling
- horizontal mirroring
- vertical mirroring

Additional parameters:

- Z
- Z2
- Autofocus Offset

---

## Adapt Stage Positions

One of the most powerful tools of the package.

Using three reference points, the plugin automatically computes:

- translation
- rotation
- scaling
- orientation

and applies the transformation to the entire acquisition map.

### Typical use case

```text
Reference Slide
        │
        ▼
 Original STG
        │
        ▼
 Three Reference Points
        │
        ▼
 New Sample
        │
        ▼
 Adapted STG
```

This allows experiments to be reproduced rapidly on multiple samples.

---

## Optimize Acquisition Paths

Large screening experiments often contain hundreds of positions.

The plugin automatically reorders positions to reduce stage displacement.

```text
Before optimization
A → Z → B → Y → C

After optimization
A → B → C → Y → Z
```

Benefits:

- reduced acquisition time
- reduced stage wear
- improved experiment robustness

---

## Remove Tagged Positions

Reference positions can be automatically removed before export.

Example:

```text
Ref_A1
Ref_A2
Ref_A3
```

---

# Multi-Well Plate Generators

Generating stage positions manually for multi-well plates is tedious.

Metamorph Companion can automatically generate acquisition layouts.

---

## Random Sampling

Useful when performing unbiased sampling.

Supported formats:

- 6-well
- 12-well
- 24-well
- 48-well
- 96-well
- Ibidi Grid-50
- custom plates

Options:

- number of positions
- sampling radius
- reference points
- well centers

---

## Regular Sampling

Useful for:

- screening
- tiling
- systematic acquisition

The generated coordinates can be exported directly as STG files.

---

# Example Workflows

## Hyperstack Reconstruction

```text
ND File
   │
   ▼
Import ND
   │
   ▼
Hyperstack
   │
   ▼
Image Analysis
```

---

## ROI Exchange

```text
Metamorph
   │
   ▼
RGN Import
   │
   ▼
ROI Manager
   │
   ▼
RGN Export
```

---

## Automated Plate Screening

```text
Plate Layout
      │
      ▼
Generate STG
      │
      ▼
Optimize Path
      │
      ▼
Metamorph Acquisition
```

---

# Installation

1. Download the latest release.
2. Copy the JAR file into:

```text
Fiji.app/plugins/
```

3. Restart Fiji.

The plugin will appear in:

```text
Plugins > Metamorph Companion
```

---
