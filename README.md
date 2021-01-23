# DeepDota

## Dota 2 Match Winner Prediction

### Data source

**22,000,000** data records were collected from the [OpenDota API](https://www.opendota.com/).

### Data preprocessing

Additional feature extraction was performed to create training/test datasets.

First, **21,800,000** data records were used to create hero statistics contained in two **(130, 130)** matrices called
the *synergy matrix*, and the *counter matrix*.

#### Synergy matrix

The *synergy matrix* informs how well a hero with a specific ID **(0-129)** works **WITH** another hero.

.   | 0     | 1     | ...   | 128   | 129   |
--- | ----- | ----- | ----- | ----- | ----- |
0   |       |       |       |       |       |
1   |       |       |       |       |       |
... |       |       |       |       |       |
128 |       |       |       |       |       |
129 |       |       |       |       |       |

#### Counter matrix

The *counter matrix* informs how well a hero with a specific ID **(0-129)** works **AGAINST** another hero.

.   | 0     | 1     | ...   | 128   | 129   |
--- | ----- | ----- | ----- | ----- | ----- |
0   |       |       |       |       |       |
1   |       |       |       |       |       |
... |       |       |       |       |       |
128 |       |       |       |       |       |
129 |       |       |       |       |       |

Then, **200,000** data records were used to create **180,000** trainings samples and **20,000** test samples.

Each sample is created from the **(10, 10)** *synergy/counter matrix* and the *label*.

#### Synergy/counter matrix

The *synergy/counter matrix* contains the statistics collected for each hero in radiant and dire team.

.   | 0   | 1   | 2   | 3   | 4   | 5   | 6   | 7   | 8   | 9   |
--- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
0   | rr  | rr  | rr  | rr  | rr  | rd  | rd  | rd  | rd  | rd  |
1   | rr  | rr  | rr  | rr  | rr  | rd  | rd  | rd  | rd  | rd  |
2   | rr  | rr  | rr  | rr  | rr  | rd  | rd  | rd  | rd  | rd  |
3   | rr  | rr  | rr  | rr  | rr  | rd  | rd  | rd  | rd  | rd  |
4   | rr  | rr  | rr  | rr  | rr  | rd  | rd  | rd  | rd  | rd  |
5   | dd  | dd  | dd  | dd  | dd  | dr  | dr  | dr  | dr  | dr  |
6   | dd  | dd  | dd  | dd  | dd  | dr  | dr  | dr  | dr  | dr  |
7   | dd  | dd  | dd  | dd  | dd  | dr  | dr  | dr  | dr  | dr  |
8   | dd  | dd  | dd  | dd  | dd  | dr  | dr  | dr  | dr  | dr  |
9   | dd  | dd  | dd  | dd  | dd  | dr  | dr  | dr  | dr  | dr  |

Where:<br/>
rr - radiant/radiant synergy score,<br/>
rd - radiant/dire counter score,<br/>
dd - dire/dire synergy score,<br/>
dr - dire/radiant counter score.

#### Label

Label informs about the match winner:<br/>
**0** - dire victory,<br/>
**1** - radiant victory.