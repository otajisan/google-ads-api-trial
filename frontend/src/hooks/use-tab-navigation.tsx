import {useState} from 'react';
import {AppPage} from '../types/page';
import {NextRouter, useRouter} from 'next/router';


export const AppPages: AppPage[] = [
  {label: 'Top', url: '/'},
];

export const useTabNavigation = () => {
  const router = useRouter();

  const state: AppPage = searchCurrentPage(router);
  const [selectedTab, setSelectedTab] = useState<AppPage>(state);

  const handleTabChange = (e: any, url: string) => {
    e.preventDefault();
    e.stopPropagation();
    setSelectedTab({
      label: e.target.textContent,
      url: url,
    } as AppPage);

    router.push(url, undefined, {shallow: true});
  };

  const handleMenuClick = (e: any, label: string, url: string) => {
    e.preventDefault();
    e.stopPropagation();
    window.location.href = url;
  };

  return {selectedTab, setSelectedTab, handleTabChange, handleMenuClick};
};

const searchCurrentPage = (router: NextRouter) => {
  let state: AppPage = {label: 'Top', url: '/'};
  AppPages.map((page) => {
    if (page.url === router.pathname) {
      state = page;
    }
  });

  return state;
};