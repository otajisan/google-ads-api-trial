import Image from 'next/image';
import Link from 'next/link';
import {Tab, Tabs} from '@mui/material';
import {AppPages, useTabNavigation} from '../hooks/use-tab-navigation';

const Header = () => {
  return (
    <div className={'sticky bg-gray-700'}>
      <HeaderLogo/>
      <NavigationBar/>
    </div>
  );
};

const NavigationBar = () => {
  const {selectedTab, setSelectedTab, handleTabChange, handleMenuClick} = useTabNavigation();

  if (selectedTab === undefined) {
    console.warn('selected tab is undefined.');
    return <></>;
  }

  return (
    <Tabs
      value={selectedTab.url}
      onChange={(e, value) => handleTabChange(e, value)}
      variant='scrollable'
      scrollButtons='auto'
    >
      {AppPages.map((page) => (
        <Tab
          key={page.url}
          label={page.label}
          value={page.url}
        />
      ))};
    </Tabs>
  );
};

const HeaderLogo = () => {
  const logoImagePath = '/img/morningcode.png';

  return (
    <div className={'flex m-4'}>
      <div className={'mr-4'}>
        <Image
          src={logoImagePath}
          alt={'MorningCode'}
          width={64}
          height={64}
          className={'flex grid col-span-4'}
        />
      </div>
      <div>
        <h1 className={'text-white pt-4'}><Link href={'/'}>Google Ads API Trial</Link></h1>
      </div>
    </div>
  );
};

export default Header;