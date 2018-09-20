package com.application.util;

import android.content.Context;
import com.application.entity.Region;
import com.application.entity.RegionGroup;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class RegionUtils {

  private List<RegionGroup> mRegionGroups;

  public RegionUtils(Context context) {
    // initialize regiongroup here
    try {
      mRegionGroups = getRegionGroupList(context);
    } catch (XmlPullParserException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static XmlPullParser getXmlPullParser(Context context)
      throws XmlPullParserException, IOException {
    InputStream is = context.getResources().getAssets().open("regions.xml");

    XmlPullParserFactory pullParserFactory;
    pullParserFactory = XmlPullParserFactory.newInstance();
    XmlPullParser parser = pullParserFactory.newPullParser();
    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
    parser.setInput(is, null);
    return parser;
  }

  public List<RegionGroup> getRegionGroups() {
    return mRegionGroups;
  }

  private List<RegionGroup> getRegionGroupList(Context context)
      throws XmlPullParserException, IOException {
    List<RegionGroup> listGroups = null;
    Region region = null;
    RegionGroup regionGroup = null;
    List<Region> regions = null;

    XmlPullParser parser = getXmlPullParser(context);
    int eventType = parser.getEventType();

    while (eventType != XmlPullParser.END_DOCUMENT) {
      String nameTag = null;
      switch (eventType) {
        case XmlPullParser.START_DOCUMENT:
          listGroups = new ArrayList<RegionGroup>();
          break;
        case XmlPullParser.START_TAG:
          nameTag = parser.getName();
          if (nameTag.equalsIgnoreCase("regionlist")) {
            listGroups = new ArrayList<RegionGroup>();
          } else if (nameTag.equalsIgnoreCase("region_group")) {
            regions = new ArrayList<Region>();
            regionGroup = new RegionGroup();
            regionGroup.setName(parser.getAttributeValue(null, "name"));
          } else if (nameTag.equalsIgnoreCase("region")) {
            int regionCode = Integer.valueOf(parser.getAttributeValue(
                null, "code"));
            String regionAlias = parser
                .getAttributeValue(null, "alias");
            String regionName = parser.nextText();
            region = new Region(regionCode, regionAlias, regionName);
            regions.add(region);
          }
          break;
        case XmlPullParser.END_TAG:
          nameTag = parser.getName();
          if (nameTag.equalsIgnoreCase("region_group")) {
            regionGroup.setRegion(regions);
            listGroups.add(regionGroup);
          }
      }
      eventType = parser.next();
    }
    return listGroups;
  }

  public int getRegionCodeFromRegionName(String regionName) {
    for (RegionGroup group : mRegionGroups) {
      for (Region region : group.getRegion()) {
        if (region.getName().equalsIgnoreCase(regionName)) {
          return region.getCode();
        }
      }
    }
    throw new IllegalArgumentException("Not found region name");
  }

  public String getRegionName(int regionCode) {
    for (RegionGroup group : mRegionGroups) {
      for (Region region : group.getRegion()) {
        if (region.getCode() == regionCode) {
          return region.getName();
        }
      }
    }
    return null;
  }

  public int getRegionCodeFromAlias(String regionAlias) {
    for (RegionGroup group : mRegionGroups) {
      for (Region region : group.getRegion()) {
        if (region.getAlias().contains(regionAlias)) {
          return region.getCode();
        }
      }
    }
    throw new IllegalArgumentException("Not found region alias");
  }

  public String[] getRegionAlias() {
    List<String> aliasList = new ArrayList<String>();
    for (RegionGroup group : mRegionGroups) {
      for (Region region : group.getRegion()) {
        aliasList.add(region.getAlias());
      }
    }
    String[] alias = new String[aliasList.size()];
    for (int i = 0; i < aliasList.size(); i++) {
      alias[i] = aliasList.get(i);
    }
    return alias;
  }

  public String[] getRegionNames() {
    List<String> nameList = new ArrayList<String>();
    for (RegionGroup group : mRegionGroups) {
      for (Region region : group.getRegion()) {
        nameList.add(region.getName());
      }
    }
    String[] regionNames = new String[nameList.size()];
    for (int i = 0; i < nameList.size(); i++) {
      regionNames[i] = nameList.get(i);
    }
    return regionNames;
  }
}
